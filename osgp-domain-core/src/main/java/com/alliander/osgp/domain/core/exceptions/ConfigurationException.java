/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.exceptions;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode = FaultCode.SERVER)
public class ConfigurationException extends PlatformException {

    /**
     * 
     */
    private static final long serialVersionUID = 4535607746025926682L;
    private static final String MESSAGE = "Platform configuration is incorrect or incomplete.";

    public ConfigurationException() {
        super(MESSAGE);
    }
}
