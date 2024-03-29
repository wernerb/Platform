/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.publiclighting.application.mapping.ws;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.MeterType;

public class PowerUsageMeterTypeConverter extends
        CustomConverter<MeterType, com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.MeterType> {

    @Override
    public com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.MeterType convert(
            final MeterType source,
            final Type<? extends com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.MeterType> destinationType) {
        // The enum values of the two types do not match (e.g. P1 -> P_1 ). The
        // JAXB MeterType
        // Ordinal value matches our domain values however.
        return com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.MeterType.values()[source.ordinal()];
    }
}