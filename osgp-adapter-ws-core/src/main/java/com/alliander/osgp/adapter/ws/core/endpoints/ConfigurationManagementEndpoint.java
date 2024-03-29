/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.core.endpoints;

import org.hibernate.validator.method.MethodConstraintViolationException;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.alliander.osgp.adapter.ws.core.application.mapping.ConfigurationManagementMapper;
import com.alliander.osgp.adapter.ws.core.application.services.ConfigurationManagementService;
import com.alliander.osgp.adapter.ws.endpointinterceptors.OrganisationIdentification;
import com.alliander.osgp.adapter.ws.schema.core.common.AsyncResponse;
import com.alliander.osgp.adapter.ws.schema.core.common.OsgpResultType;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.GetConfigurationAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.GetConfigurationAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.GetConfigurationRequest;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.GetConfigurationResponse;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.SetConfigurationAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.SetConfigurationAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.SetConfigurationRequest;
import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.SetConfigurationResponse;
import com.alliander.osgp.domain.core.exceptions.ValidationException;
import com.alliander.osgp.domain.core.valueobjects.Configuration;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;

//MethodConstraintViolationException is deprecated.
//Will by replaced by equivalent functionality defined
//by the Bean Validation 1.1 API as of Hibernate Validator 5.
@SuppressWarnings("deprecation")
@Endpoint
public class ConfigurationManagementEndpoint {

    private static final String NAMESPACE = "http://www.alliander.com/schemas/osgp/configurationmanagement/2014/10";
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationManagementEndpoint.class);
    private static final ComponentType COMPONENT_WS_CORE = ComponentType.WS_CORE;

    private final ConfigurationManagementService configurationManagementService;
    private ConfigurationManagementMapper configurationManagementMapper;

    @Autowired
    public ConfigurationManagementEndpoint(
            @Qualifier(value = "wsCoreConfigurationManagementService") final ConfigurationManagementService configurationManagementService,
            @Qualifier(value = "coreConfigurationManagementMapper") final ConfigurationManagementMapper configurationManagementMapper) {
        this.configurationManagementMapper = configurationManagementMapper;
        this.configurationManagementService = configurationManagementService;
    }

    @PayloadRoot(localPart = "SetConfigurationRequest", namespace = NAMESPACE)
    @ResponsePayload
    public SetConfigurationAsyncResponse setConfiguration(@OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetConfigurationRequest request) throws OsgpException {

        LOGGER.info("Set Configuration Request received from organisation: {} for device: {}.", organisationIdentification, request.getDeviceIdentification(),
                request.getScheduledTime());

        final SetConfigurationAsyncResponse response = new SetConfigurationAsyncResponse();

        try {
            // Get the request parameters, make sure that they are in UTC.
            // Maybe add an adapter to the service, so that all datetime are
            // converted to utc automatically.
            final DateTime scheduleTime = request.getScheduledTime() == null ? null : new DateTime(request.getScheduledTime().toGregorianCalendar())
                    .toDateTime(DateTimeZone.UTC);

            final Configuration configuration = (this.configurationManagementMapper.map(request.getConfiguration(), Configuration.class));

            final String correlationUid = this.configurationManagementService.enqueueSetConfigurationRequest(organisationIdentification,
                    request.getDeviceIdentification(), configuration, scheduleTime);

            final AsyncResponse asyncResponse = new AsyncResponse();
            asyncResponse.setCorrelationUid(correlationUid);
            asyncResponse.setDeviceId(request.getDeviceIdentification());

            response.setAsyncResponse(asyncResponse);
        } catch (final MethodConstraintViolationException e) {
            LOGGER.error("Exception: {}, StackTrace: {}", e.getMessage(), e.getStackTrace(), e);
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.WS_CORE, new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            this.handleException(e);
        }

        return response;
    }

    @PayloadRoot(localPart = "SetConfigurationAsyncRequest", namespace = NAMESPACE)
    @ResponsePayload
    public SetConfigurationResponse getSetConfigurationResponse(@OrganisationIdentification final String organisationIdentification,
            @RequestPayload final SetConfigurationAsyncRequest request) throws OsgpException {

        LOGGER.info("Get Set Configuration Response received from organisation: {} with correlationUid: {}.", organisationIdentification, request
                .getAsyncRequest().getCorrelationUid());

        final SetConfigurationResponse response = new SetConfigurationResponse();

        try {
            final ResponseMessage message = this.configurationManagementService.dequeueSetConfigurationResponse(organisationIdentification, request
                    .getAsyncRequest().getCorrelationUid());
            if (message != null) {
                response.setResult(OsgpResultType.fromValue(message.getResult().getValue()));
            }
        } catch (final Exception e) {
            this.handleException(e);
        }

        return response;
    }

    @PayloadRoot(localPart = "GetConfigurationRequest", namespace = NAMESPACE)
    @ResponsePayload
    public GetConfigurationAsyncResponse getConfiguration(@OrganisationIdentification final String organisationIdentification,
            @RequestPayload final GetConfigurationRequest request) throws OsgpException {

        LOGGER.info("Get Configuration Request received from organisation: {} for device: {}.", organisationIdentification, request.getDeviceIdentification());

        final GetConfigurationAsyncResponse response = new GetConfigurationAsyncResponse();

        try {
            final String correlationUid = this.configurationManagementService.enqueueGetConfigurationRequest(organisationIdentification,
                    request.getDeviceIdentification());

            final AsyncResponse asyncResponse = new AsyncResponse();
            asyncResponse.setCorrelationUid(correlationUid);
            asyncResponse.setDeviceId(request.getDeviceIdentification());
            response.setAsyncResponse(asyncResponse);
        } catch (final MethodConstraintViolationException e) {
            LOGGER.error("Exception get configuration: {} ", e.getMessage(), e);
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.WS_CORE, new ValidationException(e.getConstraintViolations()));
        } catch (final Exception e) {
            this.handleException(e);
        }

        return response;
    }

    @PayloadRoot(localPart = "GetConfigurationAsyncRequest", namespace = NAMESPACE)
    @ResponsePayload
    public GetConfigurationResponse getGetConfigurationResponse(@OrganisationIdentification final String organisationIdentification,
            @RequestPayload final GetConfigurationAsyncRequest request) throws OsgpException {

        LOGGER.info("GetConfigurationRequest received from organisation: {} for device: {}.", organisationIdentification, request.getAsyncRequest()
                .getDeviceId());

        final GetConfigurationResponse response = new GetConfigurationResponse();

        try {
            final ResponseMessage message = this.configurationManagementService.dequeueGetConfigurationResponse(organisationIdentification, request
                    .getAsyncRequest().getCorrelationUid());

            if (message != null) {
                response.setResult(OsgpResultType.fromValue(message.getResult().getValue()));

                if (message.getDataObject() != null) {
                    final Configuration configuration = (Configuration) message.getDataObject();
                    if (configuration != null) {

                        if (this.configurationManagementMapper == null) {
                            this.configurationManagementMapper = new ConfigurationManagementMapper();
                        }

                        response.setConfiguration(this.configurationManagementMapper.map(configuration,
                                com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.Configuration.class));
                    }
                }
            } else {
                LOGGER.debug("Get Configuration data is null");
            }
        } catch (final Exception e) {
            this.handleException(e);
        }

        return response;
    }

    private void handleException(final Exception e) throws OsgpException {
        // Rethrow exception if it already is a functional or technical exception,
        // otherwise throw new technical exception.
        if (e instanceof OsgpException) {
            LOGGER.error("Exception occurred: ", e);
            throw (OsgpException) e;
        } else {
            LOGGER.error("Exception occurred: ", e);
            throw new TechnicalException(COMPONENT_WS_CORE, e);
        }
    }
}