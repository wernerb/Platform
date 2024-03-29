/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.tariffswitching.application.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceOutputSetting;
import com.alliander.osgp.domain.core.exceptions.UnknownEntityException;
import com.alliander.osgp.domain.core.exceptions.UnregisteredDeviceException;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.valueobjects.DeviceStatus;
import com.alliander.osgp.domain.core.valueobjects.DeviceStatusMapped;
import com.alliander.osgp.domain.core.valueobjects.DomainType;
import com.alliander.osgp.domain.core.valueobjects.LightValue;
import com.alliander.osgp.domain.core.valueobjects.RelayType;
import com.alliander.osgp.domain.core.valueobjects.TariffValue;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.RequestMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

@Service(value = "domainTariffSwitchingAdHocManagementService")
@Transactional(value = "transactionManager")
public class AdHocManagementService extends AbstractService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdHocManagementService.class);

    @Autowired
    private DeviceRepository deviceRepository;

    /**
     * Constructor
     */
    public AdHocManagementService() {
        // Parameterless constructor required for transactions...
    }

    // === GET STATUS ===

    /**
     * Retrieve status of device and provide a mapped response (PublicLighting or TariffSwitching)
     *
     * @param organisationIdentification
     *            identification of organisation
     * @param deviceIdentification
     *            identification of device
     * @param allowedDomainType
     *            domain type performing requesting the status
     * @return status of device
     * @throws FunctionalException
     * @throws UnknownEntityException
     * @throws UnregisteredDeviceException
     * @throws NotAuthorizedException
     * @throws IOException
     */
    public void getStatus(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final DomainType allowedDomainType, final String messageType)
                    throws FunctionalException {

        this.findOrganisation(organisationIdentification);
        final Device device = this.findActiveDevice(deviceIdentification);

        final com.alliander.osgp.dto.valueobjects.DomainType allowedDomainTypeDto = this.domainCoreMapper.map(
                allowedDomainType, com.alliander.osgp.dto.valueobjects.DomainType.class);

        this.osgpCoreRequestMessageSender.send(new RequestMessage(correlationUid, organisationIdentification,
                deviceIdentification, allowedDomainTypeDto), messageType, device.getIpAddress());
    }

    public void handleGetStatusResponse(final com.alliander.osgp.dto.valueobjects.DeviceStatus deviceStatusDto,
            final DomainType allowedDomainType, final String deviceIdentification,
            final String organisationIdentification, final String correlationUid, final String messageType,
            final ResponseMessageResultType deviceResult, final OsgpException exception) {

        ResponseMessageResultType result = ResponseMessageResultType.OK;
        OsgpException osgpException = exception;
        DeviceStatusMapped deviceStatusMapped = null;

        try {
            if (deviceResult == ResponseMessageResultType.NOT_OK || exception != null) {
                LOGGER.error("Device Response not ok.", osgpException);
                throw osgpException;
            }

            final DeviceStatus status = this.domainCoreMapper.map(deviceStatusDto, DeviceStatus.class);

            final Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);

            final List<DeviceOutputSetting> deviceOutputSettings = device.getOutputSettings();

            final Map<Integer, DeviceOutputSetting> dosMap = new HashMap<>();
            for (final DeviceOutputSetting dos : deviceOutputSettings) {
                dosMap.put(dos.getInternalId(), dos);
            }

            deviceStatusMapped = new DeviceStatusMapped(filterTariffValues(status.getLightValues(), dosMap,
                    allowedDomainType), filterLightValues(status.getLightValues(), dosMap, allowedDomainType),
                    status.getPreferredLinkType(), status.getActualLinkType(), status.getLightType(),
                    status.getEventNotificationsMask());

        } catch (final Exception e) {
            LOGGER.error("Unexpected Exception", e);
            result = ResponseMessageResultType.NOT_OK;
            osgpException = new TechnicalException(ComponentType.UNKNOWN,
                    "Unexpected exception while retrieving response message", e);
        }

        this.webServiceResponseMessageSender.send(new ResponseMessage(correlationUid, organisationIdentification,
                deviceIdentification, result, osgpException, deviceStatusMapped));
    }

    // === CUSTOM STATUS FILTER FUNCTIONS ===

    /**
     * Filter light values based on PublicLighting domain. Only matching values will be returned.
     *
     * @param source
     *            list to filter
     * @param dosMap
     *            mapping of output settings
     * @param allowedDomainType
     *            type of domain allowed
     * @return list with filtered values or empty list when domain is not allowed.
     */
    public static List<LightValue> filterLightValues(final List<LightValue> source,
            final Map<Integer, DeviceOutputSetting> dosMap, final DomainType allowedDomainType) {

        final List<LightValue> filteredValues = new ArrayList<>();
        if (allowedDomainType != DomainType.PUBLIC_LIGHTING) {
            // Return empty list
            return filteredValues;
        }

        for (final LightValue lv : source) {
            if (dosMap.containsKey(lv.getIndex())
                    && dosMap.get(lv.getIndex()).getOutputType().domainType().equals(allowedDomainType)) {
                filteredValues.add(lv);
            }
        }

        return filteredValues;
    }

    /**
     * Filter light values based on TariffSwitching domain. Only matching values will be returned.
     *
     * @param source
     *            list to filter
     * @param dosMap
     *            mapping of output settings
     * @param allowedDomainType
     *            type of domain allowed
     * @return list with filtered values or empty list when domain is not allowed.
     */
    public static List<TariffValue> filterTariffValues(final List<LightValue> source,
            final Map<Integer, DeviceOutputSetting> dosMap, final DomainType allowedDomainType) {

        final List<TariffValue> filteredValues = new ArrayList<>();
        if (allowedDomainType != DomainType.TARIFF_SWITCHING) {
            // Return empty list
            return filteredValues;
        }

        for (final LightValue lv : source) {
            if (dosMap.containsKey(lv.getIndex())
                    && dosMap.get(lv.getIndex()).getOutputType().domainType().equals(allowedDomainType)) {
                // Map light value to tariff value
                final TariffValue tf = new TariffValue();
                tf.setIndex(lv.getIndex());
                if (dosMap.get(lv.getIndex()).getOutputType().equals(RelayType.TARIFF_REVERSED)) {
                    // Reversed means copy the 'isOn' value to the 'isHigh'
                    // value without inverting the boolean value
                    tf.setHigh(lv.isOn());
                } else {
                    // Not reversed means copy the 'isOn' value to the 'isHigh'
                    // value inverting the boolean value
                    tf.setHigh(!lv.isOn());
                }

                filteredValues.add(tf);
            }
        }

        return filteredValues;
    }
}
