/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.publiclighting.application.config;

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
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

/**
 * An application context Java configuration class. The usage of Java
 * configuration requires Spring Framework 3.0
 */
@Configuration
@ComponentScan(basePackages = { "com.alliander.osgp.domain.core", "com.alliander.osgp.adapter.ws.publiclighting" })
@EnableTransactionManagement()
@ImportResource("classpath:applicationContext.xml")
@Import({ MessagingConfig.class, PersistenceConfig.class, WebServiceConfig.class })
@PropertySource("file:${osp/osgpAdapterWsPublicLighting/config}")
public class ApplicationContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationContext.class);

    private static final String LOCAL_TIME_ZONE_IDENTIFIER = "Europe/Paris";
    private static final DateTimeZone LOCAL_TIME_ZONE = DateTimeZone.forID(LOCAL_TIME_ZONE_IDENTIFIER);
    private static final int TIME_ZONE_OFFSET_MINUTES = LOCAL_TIME_ZONE.getStandardOffset(new DateTime().getMillis()) / DateTimeConstants.MILLIS_PER_MINUTE;

    @Resource
    private Environment environment;

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

    // === Time zone config ===

    @Bean
    public String localTimeZoneIdentifier() {
        return LOCAL_TIME_ZONE_IDENTIFIER;
    }

    @Bean
    public DateTimeZone localTimeZone() {
        return LOCAL_TIME_ZONE;
    }

    @Bean
    public Integer timeZoneOffsetMinutes() {
        return TIME_ZONE_OFFSET_MINUTES;
    }

}
