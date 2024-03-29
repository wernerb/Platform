/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.valueobjects;

import java.io.Serializable;

import org.hibernate.validator.constraints.Range;

import com.alliander.osgp.domain.core.exceptions.ValidationException;
import com.alliander.osgp.domain.core.validation.LightValueConstraints;

@LightValueConstraints
public class LightValue implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -2244245336355424779L;
    private static final int MIN_INDEX = 0;
    private static final int MAX_INDEX = 6;

    private static final int MIN_DIMVALUE = 1;
    private static final int MAX_DIMVALUE = 100;

    @Range(min = MIN_INDEX, max = MAX_INDEX)
    private final Integer index;

    private boolean on;

    @Range(min = MIN_DIMVALUE, max = MAX_DIMVALUE)
    private final Integer dimValue;

    public LightValue(final Integer index, final boolean on, final Integer dimValue) throws ValidationException {
        this.index = index;
        this.on = on;
        this.dimValue = dimValue;
    }

    public Integer getIndex() {
        return this.index;
    }

    public boolean isOn() {
        return this.on;
    }

    public Integer getDimValue() {
        return this.dimValue;
    }

    public void invertIsOn() {
        this.on = !this.on;
    }
}
