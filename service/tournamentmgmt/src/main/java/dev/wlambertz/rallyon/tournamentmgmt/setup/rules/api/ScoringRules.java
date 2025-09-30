package dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api;
// TODO(review): Validate defaults per format and badminton rules

public record ScoringRules(int pointsPerGame, int gamesPerMatch, boolean winByTwo, Integer capPoints) {
}


