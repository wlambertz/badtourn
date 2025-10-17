package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api;

import java.util.Objects;

public record BracketConfig(
        BracketId id,
        String displayName,
        TournamentFormat format,
        Capacity capacity
) {
    public BracketConfig {
        Objects.requireNonNull(id, "Bracket id must not be null");
        Objects.requireNonNull(displayName, "Bracket display name must not be null");
        Objects.requireNonNull(format, "Bracket format must not be null");
    }
}
