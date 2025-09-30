package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api;
// TODO(review): Validate business constraints for overlapping windows

import java.time.Instant;

public record TimeWindow(Instant start, Instant end) {
	public TimeWindow {
		if (start == null || end == null) {
			throw new IllegalArgumentException("TimeWindow requires non-null start and end");
		}
		if (end.isBefore(start)) {
			throw new IllegalArgumentException("TimeWindow end must not be before start");
		}
	}
}


