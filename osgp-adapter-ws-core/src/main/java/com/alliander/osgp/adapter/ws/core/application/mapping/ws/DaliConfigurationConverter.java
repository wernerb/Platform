/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.core.application.mapping.ws;

import java.util.HashMap;
import java.util.Map;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.IndexAddressMap;
import com.alliander.osgp.domain.core.valueobjects.DaliConfiguration;

public class DaliConfigurationConverter
        extends
        BidirectionalConverter<com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.DaliConfiguration, DaliConfiguration> {

    @Override
    public DaliConfiguration convertTo(
            final com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.DaliConfiguration source,
            final Type<DaliConfiguration> destinationType) {

        if (source == null) {
            return null;
        }

        final Map<Integer, Integer> addressMap = new HashMap<>();
        for (final IndexAddressMap indexAddressMap : source.getIndexAddressMap()) {
            addressMap.put(indexAddressMap.getIndex(), indexAddressMap.getAddress());
        }

        return new DaliConfiguration(source.getNumberOfLights(), addressMap);
    }

    @Override
    public com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.DaliConfiguration convertFrom(
            final DaliConfiguration source,
            final Type<com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.DaliConfiguration> destinationType) {

        if (source == null) {
            return null;
        }

        final com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.DaliConfiguration dest = new com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.DaliConfiguration();

        dest.setNumberOfLights(source.getNumberOfLights());

        for (final Map.Entry<Integer, Integer> indexAddressMap : source.getIndexAddressMap().entrySet()) {
            final IndexAddressMap addressMap = new IndexAddressMap();
            addressMap.setIndex(indexAddressMap.getKey());
            addressMap.setAddress(indexAddressMap.getValue());
            dest.getIndexAddressMap().add(addressMap);
        }

        return dest;
    }
}
