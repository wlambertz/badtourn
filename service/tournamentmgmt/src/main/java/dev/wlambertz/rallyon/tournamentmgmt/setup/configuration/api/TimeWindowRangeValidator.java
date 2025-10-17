package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Instant;

public final class TimeWindowRangeValidator implements ConstraintValidator<TimeWindowRange, TimeWindow> {

    @Override
    public boolean isValid(TimeWindow value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        Instant start = value.start();
        Instant end = value.end();
        if (start == null || end == null) {
            return true;
        }
        return !end.isBefore(start);
    }
}
