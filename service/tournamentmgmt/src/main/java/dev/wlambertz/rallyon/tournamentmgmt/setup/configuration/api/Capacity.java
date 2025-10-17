package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Positive;

public record Capacity(
	@Positive(message = "Capacity amount must be positive") Integer amount,
	Unit unit
) {
	public boolean isUnbounded() {
		return amount == null;
	}

	@AssertTrue(message = "Capacity unit must be provided when amount is set")
	public boolean isUnitConsistent() {
		return amount == null || unit != null;
	}

	public enum Unit {
		PARTICIPANTS,
		PEOPLE
	}
}
