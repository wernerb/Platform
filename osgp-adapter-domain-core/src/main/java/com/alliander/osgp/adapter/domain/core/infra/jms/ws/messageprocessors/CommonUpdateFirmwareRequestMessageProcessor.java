/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.core.infra.jms.ws.messageprocessors;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.domain.core.application.services.FirmwareManagementService;
import com.alliander.osgp.adapter.domain.core.infra.jms.ws.WebServiceRequestMessageProcessor;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.shared.infra.jms.Constants;

/**
 * Class for processing common update firmware request messages
 * 
 * @author CGI
 * 
 */
@Component("domainCoreCommonUpdateFirmwareRequestMessageProcessor")
public class CommonUpdateFirmwareRequestMessageProcessor extends WebServiceRequestMessageProcessor {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonUpdateFirmwareRequestMessageProcessor.class);

    @Autowired
    @Qualifier("domainCoreFirmwareManagementService")
    private FirmwareManagementService firmwareManagementService;

    public CommonUpdateFirmwareRequestMessageProcessor() {
        super(DeviceFunction.UPDATE_FIRMWARE);
    }

    @Override
    public void processMessage(final ObjectMessage message) {
        LOGGER.debug("Processing common update firmware request message");

        String correlationUid = null;
        String messageType = null;
        String organisationIdentification = null;
        String deviceIdentification = null;
        Boolean isScheduled = null;
        Long scheduleTime = null;

        try {
            correlationUid = message.getJMSCorrelationID();
            messageType = message.getJMSType();
            organisationIdentification = message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
            deviceIdentification = message.getStringProperty(Constants.DEVICE_IDENTIFICATION);
            isScheduled = message.getBooleanProperty(Constants.IS_SCHEDULED);
            if (message.propertyExists(Constants.SCHEDULE_TIME)) {
                scheduleTime = message.getLongProperty(Constants.SCHEDULE_TIME);
            }
        } catch (final JMSException e) {
            LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
            LOGGER.debug("correlationUid: {}", correlationUid);
            LOGGER.debug("messageType: {}", messageType);
            LOGGER.debug("organisationIdentification: {}", organisationIdentification);
            LOGGER.debug("deviceIdentification: {}", deviceIdentification);
            LOGGER.debug("isScheduled: {}", isScheduled);
            return;
        }

        try {
            final String firmwareIdentification = (String) message.getObject();

            LOGGER.info("Calling application service function: {}", messageType);

            this.firmwareManagementService.updateFirmware(organisationIdentification, deviceIdentification,
                    correlationUid, firmwareIdentification, scheduleTime, messageType);

        } catch (final Exception e) {
            this.handleError(e, correlationUid, organisationIdentification, deviceIdentification, messageType);
        }
    }
}
