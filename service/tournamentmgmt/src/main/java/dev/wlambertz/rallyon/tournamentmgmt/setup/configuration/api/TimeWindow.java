package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api;
// TODO(review): Validate business constraints for overlapping windows

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

@TimeWindowRange
public record TimeWindow(
        @NotNull Instant start,
        @NotNull Instant end
) {}

