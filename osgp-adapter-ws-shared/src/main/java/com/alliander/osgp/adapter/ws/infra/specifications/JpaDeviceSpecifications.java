/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.infra.specifications;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceAuthorization;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.exceptions.ArgumentNullOrEmptyException;
import com.alliander.osgp.domain.core.specifications.DeviceSpecifications;

public class JpaDeviceSpecifications implements DeviceSpecifications {

    @Override
    public Specification<Device> forOrganisation(final Organisation organisation) throws ArgumentNullOrEmptyException {

        if (organisation == null) {
            throw new ArgumentNullOrEmptyException("organisation");
        }

        return new Specification<Device>() {
            @Override
            public Predicate toPredicate(final Root<Device> deviceRoot, final CriteriaQuery<?> query,
                    final CriteriaBuilder cb) {

                final Subquery<Long> subquery = query.subquery(Long.class);
                final Root<DeviceAuthorization> deviceAuthorizationRoot = subquery.from(DeviceAuthorization.class);
                subquery.select(deviceAuthorizationRoot.get("device").get("id").as(Long.class));
                subquery.where(cb.equal(deviceAuthorizationRoot.get("organisation"), organisation.getId()));

                return cb.in(deviceRoot.get("id")).value(subquery);
            }
        };
    }

    @Override
    public Specification<Device> hasDeviceIdentification(final String deviceIdentification)
            throws ArgumentNullOrEmptyException {

        if (StringUtils.isEmpty(deviceIdentification)) {
            throw new ArgumentNullOrEmptyException("deviceIdentification");
        }

        return new Specification<Device>() {
            @Override
            public Predicate toPredicate(final Root<Device> deviceRoot, final CriteriaQuery<?> query,
                    final CriteriaBuilder cb) {

                return cb.like(cb.upper(deviceRoot.<String> get("deviceIdentification")),
                        deviceIdentification.toUpperCase());
            }
        };
    }

    @Override
    public Specification<Device> hasCity(final String city) throws ArgumentNullOrEmptyException {

        if (StringUtils.isEmpty(city)) {
            throw new ArgumentNullOrEmptyException("city");
        }

        return new Specification<Device>() {
            @Override
            public Predicate toPredicate(final Root<Device> deviceRoot, final CriteriaQuery<?> query,
                    final CriteriaBuilder cb) {

                return cb.like(cb.upper(deviceRoot.<String> get("containerCity")), city.toUpperCase());
            }
        };
    }

    @Override
    public Specification<Device> hasPostalCode(final String postalCode) throws ArgumentNullOrEmptyException {

        if (StringUtils.isEmpty(postalCode)) {
            throw new ArgumentNullOrEmptyException("postalCode");
        }

        return new Specification<Device>() {
            @Override
            public Predicate toPredicate(final Root<Device> deviceRoot, final CriteriaQuery<?> query,
                    final CriteriaBuilder cb) {

                return cb.like(cb.upper(deviceRoot.<String> get("containerPostalCode")), postalCode.toUpperCase());
            }
        };
    }

    @Override
    public Specification<Device> hasStreet(final String street) throws ArgumentNullOrEmptyException {

        if (StringUtils.isEmpty(street)) {
            throw new ArgumentNullOrEmptyException("street");
        }

        return new Specification<Device>() {
            @Override
            public Predicate toPredicate(final Root<Device> deviceRoot, final CriteriaQuery<?> query,
                    final CriteriaBuilder cb) {

                return cb.like(cb.upper(deviceRoot.<String> get("containerStreet")), street.toUpperCase());
            }
        };
    }

    @Override
    public Specification<Device> hasNumber(final String number) throws ArgumentNullOrEmptyException {

        if (StringUtils.isEmpty(number)) {
            throw new ArgumentNullOrEmptyException("number");
        }

        return new Specification<Device>() {
            @Override
            public Predicate toPredicate(final Root<Device> deviceRoot, final CriteriaQuery<?> query,
                    final CriteriaBuilder cb) {

                return cb.like(cb.upper(deviceRoot.<String> get("containerNumber")), number.toUpperCase());
            }
        };
    }
}
