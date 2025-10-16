package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api;

import java.util.List;
import java.util.Objects;

public record DisciplineConfig(
        DisciplineId id,
        Category category,
        String displayName,
        TeamSize teamSize,
        List<BracketConfig> brackets
) {
    public DisciplineConfig {
        Objects.requireNonNull(id, "Discipline id must not be null");
        Objects.requireNonNull(category, "Discipline category must not be null");
        Objects.requireNonNull(displayName, "Discipline display name must not be null");
        Objects.requireNonNull(teamSize, "Discipline team size must not be null");
        Objects.requireNonNull(brackets, "Discipline brackets must not be null");
        brackets = List.copyOf(brackets);
    }
}
