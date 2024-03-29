/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.exceptionhandling;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.adapter.ws.schema.smartmetering.common.TechnicalFault;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;

public class TechnicalExceptionConverter extends CustomConverter<TechnicalException, TechnicalFault> {

    @Override
    public TechnicalFault convert(final TechnicalException source, final Type<? extends TechnicalFault> destinationType) {
        if (source == null) {
            return null;
        }
        final TechnicalFault destination = new TechnicalFault();
        destination.setComponent(source.getComponentType().name());
        destination.setMessage(source.getMessage());
        destination.setInnerException(source.getCause().getClass().getName());
        destination.setInnerMessage(source.getCause().getMessage());

        return destination;
    }

}
