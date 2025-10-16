package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api;

import jakarta.validation.constraints.Positive;

public record Capacity(@Positive(message = "Capacity must be positive") Integer maxParticipants) {
	public boolean isUnbounded() {
		return maxParticipants == null;
	}
}
