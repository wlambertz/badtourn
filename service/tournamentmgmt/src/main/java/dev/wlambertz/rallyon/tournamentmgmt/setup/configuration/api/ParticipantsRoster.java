package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api;

import jakarta.validation.constraints.AssertTrue;

import java.util.List;

public record ParticipantsRoster(List<Long> playerIds, List<Long> teamIds) {
	@AssertTrue(message = "Exactly one of playerIds or teamIds must be set")
	public boolean isExclusiveRoster() {
		return (playerIds == null) != (teamIds == null);
	}
}

