/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.core.application.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.EventRepository;
import com.alliander.osgp.domain.core.valueobjects.EventNotificationType;
import com.alliander.osgp.dto.valueobjects.EventNotificationMessageDataContainer;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.infra.jms.RequestMessage;

@Service(value = "domainCoreDeviceManagementService")
@Transactional(value = "transactionManager")
public class DeviceManagementService extends AbstractService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceManagementService.class);

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private EventRepository eventRepository;

    /**
     * Constructor
     */
    public DeviceManagementService() {
        // Parameterless constructor required for transactions...
    }

    // === SET EVENT NOTIFICATIONS ===

    public void setEventNotifications(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final List<EventNotificationType> eventNotifications, final String messageType)
                    throws FunctionalException {

        LOGGER.debug("setEventNotifications called with organisation {} and device {}", organisationIdentification,
                deviceIdentification);

        this.findOrganisation(organisationIdentification);
        final Device device = this.findActiveDevice(deviceIdentification);

        final List<com.alliander.osgp.dto.valueobjects.EventNotificationType> eventNotificationsDto = this.domainCoreMapper
                .mapAsList(eventNotifications, com.alliander.osgp.dto.valueobjects.EventNotificationType.class);
        final EventNotificationMessageDataContainer eventNotificationMessageDataContainer = new EventNotificationMessageDataContainer(
                eventNotificationsDto);

        this.osgpCoreRequestMessageSender.send(new RequestMessage(correlationUid, organisationIdentification,
                deviceIdentification, eventNotificationMessageDataContainer), messageType, device.getIpAddress());
    }
}
