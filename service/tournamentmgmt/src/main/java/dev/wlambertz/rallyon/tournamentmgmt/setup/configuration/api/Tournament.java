package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api;

import dev.wlambertz.rallyon.tournamentmgmt.setup.phases.api.Phase;
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.CourtAllocationPolicy;
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.MatchDurationPolicy;
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.ScoringRules;
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.SeedingPolicy;
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.TieBreakRules;
import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Builder
public record Tournament(
	long id,
	Long version,

	long organizerId,
	Visibility visibility,

	String name,
	TournamentDescription description,
	Locale locale,

	TimeWindow schedule,
	List<TimeWindow> registrationWindows,
	Venue venue,
	List<Court> courts,

	TournamentFormat format,
	List<Category> categories,
	Capacity capacity,
	TeamSize teamSize,

	RegistrationPolicy registrationPolicy,
	SeedingPolicy seedingPolicy,

	ScoringRules scoringRules,
	TieBreakRules tieBreakRules,
	MatchDurationPolicy matchDurationPolicy,

	List<Phase> phases,
	SchedulingPolicy schedulingPolicy,
	CourtAllocationPolicy courtAllocationPolicy,

	ParticipantsRoster participants,
	Map<Category, ParticipantsRoster> categoryRosters,

	TournamentStatus status,

	Instant createdAt,
	long createdByUserId,
	Instant lastModifiedAt,
	long lastModifiedByUserId
) {}
