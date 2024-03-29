/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.core.application.config;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import com.alliander.osgp.adapter.ws.infra.specifications.JpaDeviceSpecifications;
import com.alliander.osgp.adapter.ws.infra.specifications.JpaEventSpecifications;
import com.alliander.osgp.adapter.ws.shared.db.application.config.WritablePersistenceConfig;
import com.alliander.osgp.domain.core.specifications.DeviceSpecifications;
import com.alliander.osgp.domain.core.specifications.EventSpecifications;
import com.alliander.osgp.domain.core.valueobjects.FirmwareLocation;
import com.alliander.osgp.logging.domain.config.ReadOnlyLoggingConfig;
import com.alliander.osgp.shared.application.config.PagingSettings;

/**
 * An application context Java configuration class. The usage of Java
 * configuration requires Spring Framework 3.0
 */
@Configuration
@ComponentScan(basePackages = { "com.alliander.osgp.domain.core", "com.alliander.osgp.adapter.ws.core", "com.alliander.osgp.domain.logging" })
@ImportResource("classpath:applicationContext.xml")
@Import({ PersistenceConfig.class, WritablePersistenceConfig.class, ReadOnlyLoggingConfig.class  })
@PropertySource("file:${osp/osgpAdapterWsCore/config}")
public class ApplicationContext {

    private static final String PROPERTY_NAME_DEFAULT_PROTOCOL = "default.protocol";
    private static final String PROPERTY_NAME_DEFAULT_PROTOCOL_VERSION = "default.protocol.version";

    private static final String PROPERTY_NAME_RECENT_DEVICES_PERIOD = "recent.devices.period";

    private static final String PROPERTY_NAME_FIRMWARE_DOMAIN = "firmware.domain";
    private static final String PROPERTY_NAME_FIRMWARE_PATH = "firmware.path";
    private static final String PROPERTY_NAME_FIRMWARE_FILE_EXTENSION = "firmware.fileExtension";
    private static final String PROPERTY_NAME_PAGING_MAXIMUM_PAGE_SIZE = "paging.maximum.pagesize";
    private static final String PROPERTY_NAME_PAGING_DEFAULT_PAGE_SIZE = "paging.default.pagesize";

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationContext.class);

    private static final String LOCAL_TIME_ZONE_IDENTIFIER = "Europe/Paris";
    private static final DateTimeZone LOCAL_TIME_ZONE = DateTimeZone.forID(LOCAL_TIME_ZONE_IDENTIFIER);
    private static final int TIME_ZONE_OFFSET_MINUTES = LOCAL_TIME_ZONE.getStandardOffset(new DateTime().getMillis()) / DateTimeConstants.MILLIS_PER_MINUTE;

    @Resource
    private Environment environment;

    @Bean
    public String defaultProtocol() {
        return this.environment.getRequiredProperty(PROPERTY_NAME_DEFAULT_PROTOCOL);
    }

    @Bean
    public String defaultProtocolVersion() {
        return this.environment.getRequiredProperty(PROPERTY_NAME_DEFAULT_PROTOCOL_VERSION);
    }

    @Bean
    public Integer recentDevicesPeriod() {
        return Integer.parseInt(this.environment.getRequiredProperty(PROPERTY_NAME_RECENT_DEVICES_PERIOD));
    }

    /**
     * @return
     */
    @Bean
    public PagingSettings pagingSettings() {
        final PagingSettings settings = new PagingSettings(Integer.parseInt(this.environment.getRequiredProperty(PROPERTY_NAME_PAGING_MAXIMUM_PAGE_SIZE)),
                Integer.parseInt(this.environment.getRequiredProperty(PROPERTY_NAME_PAGING_DEFAULT_PAGE_SIZE)));

        return settings;
    }

    /**
     * @return
     */
    @Bean
    public FirmwareLocation firmwareLocation() {
        return new FirmwareLocation(this.environment.getProperty(PROPERTY_NAME_FIRMWARE_DOMAIN), this.environment.getProperty(PROPERTY_NAME_FIRMWARE_PATH),
                this.environment.getProperty(PROPERTY_NAME_FIRMWARE_FILE_EXTENSION));
    }

    /**
     * @return
     */
    @Bean
    public EventSpecifications eventSpecifications() {
        return new JpaEventSpecifications();
    }

    @Bean
    public DeviceSpecifications deviceSpecifications() {
        return new JpaDeviceSpecifications();
    }

    /**
     * @return
     */
    @Bean
    public LocalValidatorFactoryBean validator() {
        final LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
        final org.springframework.core.io.Resource[] resources = { new ClassPathResource("constraint-mappings.xml") };
        localValidatorFactoryBean.setMappingLocations(resources);
        return localValidatorFactoryBean;
    }

    /**
     * @return
     */
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        final MethodValidationPostProcessor m = new MethodValidationPostProcessor();
        m.setValidatorFactory(this.validator());
        return m;
    }

    @Bean
    public String localTimeZoneIdentifier() {
        return LOCAL_TIME_ZONE_IDENTIFIER;
    }

    @Bean
    public DateTimeZone localTimeZone() {
        return LOCAL_TIME_ZONE;
    }

    @Bean
    public int timeZoneOffsetMinutes() {
        return TIME_ZONE_OFFSET_MINUTES;
    }
}
