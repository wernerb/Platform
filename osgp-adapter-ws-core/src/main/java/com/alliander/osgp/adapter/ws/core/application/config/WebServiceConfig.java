/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.core.application.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.server.endpoint.adapter.DefaultMethodEndpointAdapter;
import org.springframework.ws.server.endpoint.adapter.method.MarshallingPayloadMethodProcessor;
import org.springframework.ws.server.endpoint.adapter.method.MethodArgumentResolver;
import org.springframework.ws.server.endpoint.adapter.method.MethodReturnValueHandler;

import com.alliander.osgp.adapter.ws.core.application.exceptionhandling.DetailSoapFaultMappingExceptionResolver;
import com.alliander.osgp.adapter.ws.core.application.exceptionhandling.SoapFaultMapper;
import com.alliander.osgp.adapter.ws.endpointinterceptors.AnnotationMethodArgumentResolver;
import com.alliander.osgp.adapter.ws.endpointinterceptors.CertificateAndSoapHeaderAuthorizationEndpointInterceptor;
import com.alliander.osgp.adapter.ws.endpointinterceptors.OrganisationIdentification;
import com.alliander.osgp.adapter.ws.endpointinterceptors.SoapHeaderEndpointInterceptor;
import com.alliander.osgp.adapter.ws.endpointinterceptors.WebServiceMonitorInterceptor;
import com.alliander.osgp.adapter.ws.endpointinterceptors.X509CertificateRdnAttributeValueEndpointInterceptor;

@Configuration
@PropertySource("file:${osp/osgpAdapterWsCore/config}")
public class WebServiceConfig {

    private static final String PROPERTY_NAME_MARSHALLER_CONTEXT_PATH_DEVICE_INSTALLATION = "jaxb2.marshaller.context.path.deviceinstallation";
    private static final String PROPERTY_NAME_MARSHALLER_CONTEXT_PATH_DEVICE_MANAGEMENT = "jaxb2.marshaller.context.path.devicemanagement";
    private static final String PROPERTY_NAME_MARSHALLER_CONTEXT_PATH_AD_HOC_MANAGEMENT = "jaxb2.marshaller.context.path.adhocmanagement";
    private static final String PROPERTY_NAME_MARSHALLER_CONTEXT_PATH_FIRMWARE_MANAGEMENT = "jaxb2.marshaller.context.path.firmwaremanagement";
    private static final String PROPERTY_NAME_MARSHALLER_CONTEXT_PATH_CONFIGURATION_MANAGEMENT = "jaxb2.marshaller.context.path.configurationmanagement";

    private static final String ORGANISATION_IDENTIFICATION_HEADER = "OrganisationIdentification";
    private static final String ORGANISATION_IDENTIFICATION_CONTEXT = ORGANISATION_IDENTIFICATION_HEADER;

    private static final String USER_NAME_HEADER = "UserName";

    private static final String APPLICATION_NAME_HEADER = "ApplicationName";

    private static final String X509_RDN_ATTRIBUTE_ID = "cn";
    private static final String X509_RDN_ATTRIBUTE_VALUE_CONTEXT_PROPERTY_NAME = "CommonNameSet";

    private static final Logger LOGGER = LoggerFactory.getLogger(WebServiceConfig.class);

    @Resource
    private Environment environment;

    // === COMMON MARSHALLERS ===

    /**
     * Method for creating the Marshaller for device installation.
     * 
     * @return Jaxb2Marshaller
     */
    @Bean
    public Jaxb2Marshaller deviceInstallationMarshaller() {
        LOGGER.debug("Creating Device Installation Marshaller Bean");

        final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

        marshaller.setContextPath(this.environment.getRequiredProperty(PROPERTY_NAME_MARSHALLER_CONTEXT_PATH_DEVICE_INSTALLATION));

        return marshaller;
    }

    /**
     * Method for creating the Marshaller for device management.
     * 
     * @return Jaxb2Marshaller
     */
    @Bean
    public Jaxb2Marshaller deviceManagementMarshaller() {
        LOGGER.debug("Creating Device Management Marshaller Bean");

        final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

        marshaller.setContextPath(this.environment.getRequiredProperty(PROPERTY_NAME_MARSHALLER_CONTEXT_PATH_DEVICE_MANAGEMENT));

        return marshaller;
    }

    /**
     * Method for creating the Marshaller for ad hoc management.
     * 
     * @return Jaxb2Marshaller
     */
    @Bean
    public Jaxb2Marshaller adHocManagementMarshaller() {
        LOGGER.debug("Creating Ad Hoc Management Marshaller Bean");

        final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

        marshaller.setContextPath(this.environment.getRequiredProperty(PROPERTY_NAME_MARSHALLER_CONTEXT_PATH_AD_HOC_MANAGEMENT));

        return marshaller;
    }

    /**
     * Method for creating the Marshaller for firmware management.
     * 
     * @return Jaxb2Marshaller
     */
    @Bean
    public Jaxb2Marshaller firmwareManagementMarshaller() {
        LOGGER.debug("Creating Firmware Management Marshaller Bean");

        final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

        marshaller.setContextPath(this.environment.getRequiredProperty(PROPERTY_NAME_MARSHALLER_CONTEXT_PATH_FIRMWARE_MANAGEMENT));

        return marshaller;
    }

    /**
     * Method for creating the Marshaller for configuration management.
     * 
     * @return Jaxb2Marshaller
     */
    @Bean
    public Jaxb2Marshaller configurationManagementMarshaller() {
        LOGGER.debug("Creating Configuration Management Marshaller Bean");

        final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

        marshaller.setContextPath(this.environment.getRequiredProperty(PROPERTY_NAME_MARSHALLER_CONTEXT_PATH_CONFIGURATION_MANAGEMENT));

        return marshaller;
    }

    /**
     * Method for creating the Marshalling Payload Method Processor for device installation.
     * 
     * @return MarshallingPayloadMethodProcessor
     */
    @Bean
    public MarshallingPayloadMethodProcessor deviceInstallationMarshallingPayloadMethodProcessor() {
        LOGGER.debug("Creating Device Installation Marshalling Payload Method Processor Bean");

        final MarshallingPayloadMethodProcessor marshallingPayloadMethodProcessor = new MarshallingPayloadMethodProcessor(this.deviceInstallationMarshaller(),
                this.deviceInstallationMarshaller());

        return marshallingPayloadMethodProcessor;
    }

    /**
     * Method for creating the Marshalling Payload Method Processor for device management.
     * 
     * @return MarshallingPayloadMethodProcessor
     */
    @Bean
    public MarshallingPayloadMethodProcessor deviceManagementMarshallingPayloadMethodProcessor() {
        LOGGER.debug("Creating Device Management Marshalling Payload Method Processor Bean");

        final MarshallingPayloadMethodProcessor marshallingPayloadMethodProcessor = new MarshallingPayloadMethodProcessor(this.deviceManagementMarshaller(),
                this.deviceManagementMarshaller());

        return marshallingPayloadMethodProcessor;
    }

    /**
     * Method for creating the Marshalling Payload Method Processor for ad hoc management.
     * 
     * @return MarshallingPayloadMethodProcessor
     */
    @Bean
    public MarshallingPayloadMethodProcessor adHocManagementMarshallingPayloadMethodProcessor() {
        LOGGER.debug("Creating Ad Hoc Management Marshalling Payload Method Processor Bean");

        final MarshallingPayloadMethodProcessor marshallingPayloadMethodProcessor = new MarshallingPayloadMethodProcessor(this.adHocManagementMarshaller(),
                this.adHocManagementMarshaller());

        return marshallingPayloadMethodProcessor;
    }

    /**
     * Method for creating the Marshalling Payload Method Processor for firmware management.
     * 
     * @return MarshallingPayloadMethodProcessor
     */
    @Bean
    public MarshallingPayloadMethodProcessor firmwareManagementMarshallingPayloadMethodProcessor() {
        LOGGER.debug("Creating Firmware Management Marshalling Payload Method Processor Bean");

        final MarshallingPayloadMethodProcessor marshallingPayloadMethodProcessor = new MarshallingPayloadMethodProcessor(this.firmwareManagementMarshaller(),
                this.firmwareManagementMarshaller());

        return marshallingPayloadMethodProcessor;
    }

    /**
     * Method for creating the Marshalling Payload Method Processor for configuration management.
     * 
     * @return MarshallingPayloadMethodProcessor
     */
    @Bean
    public MarshallingPayloadMethodProcessor configurationManagementMarshallingPayloadMethodProcessor() {
        LOGGER.debug("Creating Configuration Management Marshalling Payload Method Processor Bean");

        final MarshallingPayloadMethodProcessor marshallingPayloadMethodProcessor = new MarshallingPayloadMethodProcessor(
                this.configurationManagementMarshaller(), this.configurationManagementMarshaller());

        return marshallingPayloadMethodProcessor;
    }

    /**
     * Method for creating the Default Method Endpoint Adapter.
     * 
     * @return DefaultMethodEndpointAdapter
     */
    @Bean
    public DefaultMethodEndpointAdapter defaultMethodEndpointAdapter() {
        LOGGER.debug("Creating Default Method Endpoint Adapter Bean");

        final DefaultMethodEndpointAdapter defaultMethodEndpointAdapter = new DefaultMethodEndpointAdapter();

        final List<MethodArgumentResolver> methodArgumentResolvers = new ArrayList<MethodArgumentResolver>();

        // Common method argument resolvers
        methodArgumentResolvers.add(this.adHocManagementMarshallingPayloadMethodProcessor());
        methodArgumentResolvers.add(this.deviceInstallationMarshallingPayloadMethodProcessor());
        methodArgumentResolvers.add(this.deviceManagementMarshallingPayloadMethodProcessor());
        methodArgumentResolvers.add(this.firmwareManagementMarshallingPayloadMethodProcessor());
        methodArgumentResolvers.add(this.configurationManagementMarshallingPayloadMethodProcessor());

        methodArgumentResolvers.add(new AnnotationMethodArgumentResolver(ORGANISATION_IDENTIFICATION_CONTEXT, OrganisationIdentification.class));
        defaultMethodEndpointAdapter.setMethodArgumentResolvers(methodArgumentResolvers);

        final List<MethodReturnValueHandler> methodReturnValueHandlers = new ArrayList<MethodReturnValueHandler>();

        // Common method return value handlers
        methodReturnValueHandlers.add(this.adHocManagementMarshallingPayloadMethodProcessor());
        methodReturnValueHandlers.add(this.deviceInstallationMarshallingPayloadMethodProcessor());
        methodReturnValueHandlers.add(this.deviceManagementMarshallingPayloadMethodProcessor());
        methodReturnValueHandlers.add(this.firmwareManagementMarshallingPayloadMethodProcessor());
        methodReturnValueHandlers.add(this.configurationManagementMarshallingPayloadMethodProcessor());

        defaultMethodEndpointAdapter.setMethodReturnValueHandlers(methodReturnValueHandlers);

        return defaultMethodEndpointAdapter;
    }

    @Bean
    public DetailSoapFaultMappingExceptionResolver exceptionResolver() {
        LOGGER.debug("Creating Detail Soap Fault Mapping Exception Resolver Bean");
        final DetailSoapFaultMappingExceptionResolver exceptionResolver = new DetailSoapFaultMappingExceptionResolver(new SoapFaultMapper());
        exceptionResolver.setOrder(1);

        final Properties props = new Properties();
        props.put("com.alliander.osgp.shared.exceptionhandling.FunctionalException", "SERVER");
        exceptionResolver.setExceptionMappings(props);
        return exceptionResolver;
    }

    /**
     * @return
     */
    @Bean
    public SoapHeaderEndpointInterceptor organisationIdentificationInterceptor() {
        LOGGER.debug("Creating Organisation Identification Interceptor Bean");

        return new SoapHeaderEndpointInterceptor(ORGANISATION_IDENTIFICATION_HEADER, ORGANISATION_IDENTIFICATION_CONTEXT);
    }

    /**
     * @return
     */
    @Bean
    public X509CertificateRdnAttributeValueEndpointInterceptor x509CertificateSubjectCnEndpointInterceptor() {
        LOGGER.debug("Creating X509 Certificate Subject CN Endpoint Interceptor Bean");

        return new X509CertificateRdnAttributeValueEndpointInterceptor(X509_RDN_ATTRIBUTE_ID, X509_RDN_ATTRIBUTE_VALUE_CONTEXT_PROPERTY_NAME);
    }

    /**
     * @return
     */
    @Bean
    public CertificateAndSoapHeaderAuthorizationEndpointInterceptor organisationIdentificationInCertificateCnEndpointInterceptor() {
        LOGGER.debug("Creating Certificate And Soap Header Authorization Endpoint Interceptor Bean");

        return new CertificateAndSoapHeaderAuthorizationEndpointInterceptor(X509_RDN_ATTRIBUTE_VALUE_CONTEXT_PROPERTY_NAME, ORGANISATION_IDENTIFICATION_CONTEXT);
    }

    @Bean
    public WebServiceMonitorInterceptor webServiceMonitorInterceptor() {
        LOGGER.debug("Creating Web Service Monitor Interceptor Bean");

        return new WebServiceMonitorInterceptor(ORGANISATION_IDENTIFICATION_HEADER, USER_NAME_HEADER, APPLICATION_NAME_HEADER);
    }
}
