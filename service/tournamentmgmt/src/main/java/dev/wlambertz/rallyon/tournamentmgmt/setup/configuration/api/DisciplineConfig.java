package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;
import java.util.Objects;

public record DisciplineConfig(
        @Positive long id,
        @NotNull Category category,
        @NotBlank String displayName,
        @NotNull TeamSize teamSize,
        @NotNull @Valid List<BracketConfig> brackets
) {
    public DisciplineConfig {
        if (id <= 0) {
            throw new IllegalArgumentException("Discipline id must be positive");
        }
        Objects.requireNonNull(category, "Discipline category must not be null");
        Objects.requireNonNull(displayName, "Discipline display name must not be null");
        Objects.requireNonNull(teamSize, "Discipline team size must not be null");
        Objects.requireNonNull(brackets, "Discipline brackets must not be null");
        brackets = List.copyOf(brackets);
    }

    public static DisciplineConfig of(
            long id,
            Category category,
            String displayName,
            TeamSize teamSize,
            List<BracketConfig> brackets
    ) {
        return new DisciplineConfig(id, category, displayName, teamSize, brackets);
    }
}
