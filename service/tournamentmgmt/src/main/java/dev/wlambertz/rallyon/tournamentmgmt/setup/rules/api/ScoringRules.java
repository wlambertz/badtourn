package dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api;
// TODO(review): Validate defaults per format and badminton rules

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.Objects;

public record ScoringRules(
	@NotNull Type type,
	@Positive int pointsPerGame,
	@Positive int gamesPerMatch,
	boolean winByTwo,
	@Positive Integer capPoints
) {
	public ScoringRules {
		Objects.requireNonNull(type, "Scoring rules type must not be null");
		if (type.presetSpec() != null && !type.presetSpec().matches(pointsPerGame, gamesPerMatch, winByTwo, capPoints)) {
			throw new IllegalArgumentException("Values must match preset definition for type " + type);
		}
	}

	public static ScoringRules twoByTwentyOne() {
		return fromPreset(Type.TWO_BY_TWENTY_ONE);
	}

	public static ScoringRules threeByFifteen() {
		return fromPreset(Type.THREE_BY_FIFTEEN);
	}

	public static ScoringRules custom(int pointsPerGame, int gamesPerMatch, boolean winByTwo, Integer capPoints) {
		return new ScoringRules(Type.CUSTOM, pointsPerGame, gamesPerMatch, winByTwo, capPoints);
	}

	private static ScoringRules fromPreset(Type type) {
		PresetSpec spec = Objects.requireNonNull(type.presetSpec(), "Type " + type + " is not a preset");
		return new ScoringRules(type, spec.pointsPerGame(), spec.gamesPerMatch(), spec.winByTwo(), spec.capPoints());
	}

	@AssertTrue(message = "Cap points must exceed points per game when defined")
	public boolean isCapPointsConsistent() {
		return capPoints == null || capPoints > pointsPerGame;
	}

	private record PresetSpec(int pointsPerGame, int gamesPerMatch, boolean winByTwo, Integer capPoints) {
		boolean matches(int pointsPerGame, int gamesPerMatch, boolean winByTwo, Integer capPoints) {
			return this.pointsPerGame == pointsPerGame
				&& this.gamesPerMatch == gamesPerMatch
				&& this.winByTwo == winByTwo
				&& Objects.equals(this.capPoints, capPoints);
		}
	}

	public enum Type {
		TWO_BY_TWENTY_ONE(new PresetSpec(21, 3, true, 30)),
		THREE_BY_FIFTEEN(new PresetSpec(15, 3, true, 21)),
		CUSTOM(null);

		private final PresetSpec presetSpec;

		Type(PresetSpec presetSpec) {
			this.presetSpec = presetSpec;
		}

		PresetSpec presetSpec() {
			return presetSpec;
		}
	}
}
