/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.core.application.mapping.ws;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.MeterType;

public class MeterTypeConverter extends
        BidirectionalConverter<com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.MeterType, MeterType> {

    @Override
    public MeterType convertTo(
            final com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.MeterType source,
            final Type<MeterType> destinationType) {
        // The enum values of the two types do not match (e.g. P_1 -> P1). The
        // JAXB MeterType
        // String value matches our domain values however.
        return MeterType.valueOf(source.value());
    }

    @Override
    public com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.MeterType convertFrom(
            final MeterType source,
            final Type<com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.MeterType> destinationType) {
        // The enum values of the two types do not match (e.g. P_1 -> P1). The
        // JAXB MeterType
        // String value matches our domain values however.
        return com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.MeterType.fromValue(source.toString());
    }
}
