package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.mapping;

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TournamentStatus;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Visibility;
import java.time.Instant;

public record TournamentFlat(
    Long id,
    Long version,
    long organizerId,
    Visibility visibility,
    String name,
    TournamentStatus status,
    Instant createdAt,
    long createdByUserId,
    Instant lastModifiedAt,
    long lastModifiedByUserId
) {}
