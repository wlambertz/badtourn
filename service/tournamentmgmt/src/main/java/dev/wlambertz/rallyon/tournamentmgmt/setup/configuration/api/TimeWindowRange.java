package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = TimeWindowRangeValidator.class)
@Target({ElementType.TYPE, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TimeWindowRange {
    String message() default "TimeWindow end must not be before start";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
