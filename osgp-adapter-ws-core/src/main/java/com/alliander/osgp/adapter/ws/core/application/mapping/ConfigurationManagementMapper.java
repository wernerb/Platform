/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.core.application.mapping;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;

import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.ws.core.application.mapping.ws.DaliConfigurationConverter;
import com.alliander.osgp.adapter.ws.core.application.mapping.ws.MeterTypeConverter;

@Component(value = "coreConfigurationManagementMapper")
public class ConfigurationManagementMapper extends ConfigurableMapper {
    @Override
    public void configure(final MapperFactory mapperFactory) {
        mapperFactory.getConverterFactory().registerConverter(new MeterTypeConverter());
        mapperFactory.getConverterFactory().registerConverter(new DaliConfigurationConverter());
    }
}