package com.alliander.osgp.domain.core.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LightTypeAndConfigurationValidator.class)
@Documented
public @interface LightTypeAndConfiguration {

    String message() default "Light type (e.g. relay, dali) must match configuration type (e.g. relay, dali).";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}