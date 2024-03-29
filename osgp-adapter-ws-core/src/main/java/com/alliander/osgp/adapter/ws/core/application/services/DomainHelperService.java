/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.core.application.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.exceptions.NotAuthorizedException;
import com.alliander.osgp.domain.core.exceptions.UnknownEntityException;
import com.alliander.osgp.domain.core.exceptions.UnregisteredDeviceException;
import com.alliander.osgp.domain.core.services.DeviceDomainService;
import com.alliander.osgp.domain.core.services.OrganisationDomainService;
import com.alliander.osgp.domain.core.services.SecurityService;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.domain.core.valueobjects.PlatformFunction;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;

@Service(value = "wsCoreDomainHelperService")
public class DomainHelperService {

    private static final ComponentType COMPONENT_TYPE = ComponentType.WS_CORE;

    @Autowired
    private DeviceDomainService deviceDomainService;

    @Autowired
    private OrganisationDomainService organisationDomainService;

    @Autowired
    private SecurityService securityService;

    public Device findDevice(final String deviceIdentification) throws FunctionalException {
        Device device;
        try {
            device = this.deviceDomainService.searchDevice(deviceIdentification);
        } catch (final UnknownEntityException e) {
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_DEVICE, COMPONENT_TYPE, e);
        }
        return device;
    }

    public Device findActiveDevice(final String deviceIdentification) throws FunctionalException {
        Device device;
        try {
            device = this.deviceDomainService.searchActiveDevice(deviceIdentification);
        } catch (final UnregisteredDeviceException e) {
            throw new FunctionalException(FunctionalExceptionType.UNREGISTERED_DEVICE, COMPONENT_TYPE, e);
        } catch (final UnknownEntityException e) {
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_DEVICE, COMPONENT_TYPE, e);
        }
        return device;
    }

    public Organisation findOrganisation(final String organisationIdentification) throws FunctionalException {
        Organisation organisation;
        try {
            organisation = this.organisationDomainService.searchOrganisation(organisationIdentification);
        } catch (final UnknownEntityException e) {
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_ORGANISATION, COMPONENT_TYPE, e);
        }
        return organisation;
    }

    public void isAllowed(final Organisation organisation, final PlatformFunction platformFunction) throws FunctionalException {
        try {
            this.securityService.checkAuthorization(organisation, platformFunction);
        } catch (final NotAuthorizedException e) {
            throw new FunctionalException(FunctionalExceptionType.UNAUTHORIZED, COMPONENT_TYPE, e);
        }
    }

    public void isAllowed(final Organisation organisation, final Device device, final DeviceFunction deviceFunction) throws FunctionalException {
        try {
            this.securityService.checkAuthorization(organisation, device, deviceFunction);
        } catch (final NotAuthorizedException e) {
            throw new FunctionalException(FunctionalExceptionType.UNAUTHORIZED, COMPONENT_TYPE, e);
        }
    }
}
