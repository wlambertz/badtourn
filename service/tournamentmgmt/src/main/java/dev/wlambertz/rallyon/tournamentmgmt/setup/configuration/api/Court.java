package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record Court(long id, @NotBlank String label, @NotNull Availability availability, @NotNull Type type) {

	public enum Availability {
		AVAILABLE,
		IN_USE,
		UNAVAILABLE
	}

	public enum Type {
		STANDARD,
		SINGLES_ONLY
	}
}
