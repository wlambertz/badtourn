package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api;
// TODO(review): Confirm mapping between enum and numeric size

public enum TeamSize {
	SINGLES(1),
	DOUBLES(2);

	public final int size;

	TeamSize(int size) {
		this.size = size;
	}
}


