package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api;
// TODO(review): Decide capacity per tournament vs per category

public record Capacity(int maxParticipants) {
	public Capacity {
		if (maxParticipants <= 0) {
			throw new IllegalArgumentException("Capacity must be positive");
		}
	}
}


