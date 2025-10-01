package dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api;
// TODO(review): Confirm tiebreak criteria order and Swiss Buchholz usage

public record TieBreakRules(boolean useSetDifference, boolean usePointsRatio, boolean useBuchholz) {
}


