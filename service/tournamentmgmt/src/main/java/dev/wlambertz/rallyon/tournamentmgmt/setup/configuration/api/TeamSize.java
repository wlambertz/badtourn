package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api;

public enum TeamSize {
	SINGLES(1),
	DOUBLES(2);

	public final int size;

	TeamSize(int size) {
		this.size = size;
	}
}


