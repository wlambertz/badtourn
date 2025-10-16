package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api;

import java.util.Objects;

/**
 * Identifier for a tournament bracket (Teilnehmerfeld).
 */
public record BracketId(String value) {

    public BracketId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("BracketId must not be blank");
        }
    }

    public static BracketId of(String value) {
        return new BracketId(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
