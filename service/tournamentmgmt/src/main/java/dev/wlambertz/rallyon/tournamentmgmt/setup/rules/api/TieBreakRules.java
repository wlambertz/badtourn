package dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api;

import jakarta.validation.constraints.NotNull;

import java.util.Objects;

public record TieBreakRules(
	@NotNull Type type,
	boolean useSetDifference,
	boolean usePointsRatio,
	boolean useBuchholz
) {

	public TieBreakRules {
		Objects.requireNonNull(type, "Tie-break rules type must not be null");
		if (type.presetSpec() != null && !type.presetSpec().matches(useSetDifference, usePointsRatio, useBuchholz)) {
			throw new IllegalArgumentException("Values must match preset definition for type " + type);
		}
	}

	public static TieBreakRules headToHead() {
		return fromPreset(Type.HEAD_TO_HEAD);
	}

	public static TieBreakRules pointsRatio() {
		return fromPreset(Type.POINTS_RATIO);
	}

	public static TieBreakRules swissStrength() {
		return fromPreset(Type.SWISS_STRENGTH);
	}

	public static TieBreakRules custom(boolean useSetDifference, boolean usePointsRatio, boolean useBuchholz) {
		return new TieBreakRules(Type.CUSTOM, useSetDifference, usePointsRatio, useBuchholz);
	}

	private static TieBreakRules fromPreset(Type type) {
		PresetSpec spec = Objects.requireNonNull(type.presetSpec(), "Type " + type + " is not a preset");
		return new TieBreakRules(type, spec.useSetDifference(), spec.usePointsRatio(), spec.useBuchholz());
	}

	private record PresetSpec(boolean useSetDifference, boolean usePointsRatio, boolean useBuchholz) {
		boolean matches(boolean useSetDifference, boolean usePointsRatio, boolean useBuchholz) {
			return this.useSetDifference == useSetDifference
				&& this.usePointsRatio == usePointsRatio
				&& this.useBuchholz == useBuchholz;
		}
	}

	public enum Type {
		HEAD_TO_HEAD(new PresetSpec(true, false, false)),
		POINTS_RATIO(new PresetSpec(false, true, false)),
		SWISS_STRENGTH(new PresetSpec(true, true, true)),
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

