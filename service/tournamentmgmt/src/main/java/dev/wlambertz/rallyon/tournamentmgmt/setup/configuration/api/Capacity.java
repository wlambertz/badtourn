package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api;
// TODO(review): Decide capacity per tournament vs per category

import jakarta.validation.constraints.Positive;

public record Capacity(@Positive(message = "Capacity must be positive") Integer maxParticipants) {
	public boolean isUnbounded() {
		return maxParticipants == null;
	}
}
