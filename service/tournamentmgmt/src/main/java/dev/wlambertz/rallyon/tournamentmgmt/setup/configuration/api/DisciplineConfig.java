package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Objects;

public record DisciplineConfig(
        @NotNull DisciplineId id,
        @NotNull Category category,
        @NotBlank String displayName,
        @NotNull TeamSize teamSize,
        @NotNull @Valid List<BracketConfig> brackets
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
