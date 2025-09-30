package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api;
// TODO(review): Decide roster ownership, constraints, and cross-BC references

import java.util.List;

public record ParticipantsRoster(List<Long> playerIds, List<Long> teamIds) {
}


