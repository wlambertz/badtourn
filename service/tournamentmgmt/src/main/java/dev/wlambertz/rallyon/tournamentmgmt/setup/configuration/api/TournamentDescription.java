package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api;
// TODO(review): Confirm description max length and nullability

import java.util.Objects;

public final class TournamentDescription {
	private final String value;

	public TournamentDescription(String value) {
		if (value == null) {
			throw new IllegalArgumentException("TournamentDescription must not be null");
		}
		if (value.length() > 2000) {
			throw new IllegalArgumentException("TournamentDescription must be <= 2000 characters");
		}
		this.value = value;
	}

	public String value() {
		return value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof TournamentDescription that)) return false;
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


