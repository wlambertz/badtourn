package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api;
// TODO(review): Confirm name length and validation rules

import java.util.Objects;

public final class TournamentName {
	private final String value;

	public TournamentName(String value) {
		if (value == null || value.isBlank()) {
			throw new IllegalArgumentException("TournamentName must not be blank");
		}
		if (value.length() > 200) {
			throw new IllegalArgumentException("TournamentName must be <= 200 characters");
		}
		this.value = value;
	}

	public String value() {
		return value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof TournamentName that)) return false;
		return value.equals(that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}

	@Override
	public String toString() {
		return value;
	}
}


