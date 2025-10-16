package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

@TimeWindowRange
public record TimeWindow(
        @NotNull Instant start,
        @NotNull Instant end
) {}
