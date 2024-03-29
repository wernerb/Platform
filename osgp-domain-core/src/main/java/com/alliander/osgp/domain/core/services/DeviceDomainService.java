/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.exceptions.UnknownEntityException;
import com.alliander.osgp.domain.core.exceptions.UnregisteredDeviceException;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.validation.Identification;

@Service
@Validated
@Transactional(value = "transactionManager")
public class DeviceDomainService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceDomainService.class);

    @Autowired
    private DeviceRepository deviceRepository;

    public Device searchDevice(@Identification final String deviceIdentification) throws UnknownEntityException {

        final Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);

        if (device == null) {
            throw new UnknownEntityException(Device.class, deviceIdentification);
        }

        return device;
    }

    public Device searchActiveDevice(@Identification final String deviceIdentification) throws UnregisteredDeviceException, UnknownEntityException {

        final Device device = this.searchDevice(deviceIdentification);

        if (!device.isActivated() || !device.isPublicKeyPresent()) {
            throw new UnregisteredDeviceException(deviceIdentification);
        }
        return device;
    }
}
