package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api;

/**
 * Identifier for a tournament discipline (e.g. Mixed Doubles).
 */
public record DisciplineId(String value) {

    public DisciplineId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("DisciplineId must not be blank");
        }
    }

    public static DisciplineId of(String value) {
        return new DisciplineId(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
