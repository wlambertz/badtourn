package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api;

import dev.wlambertz.rallyon.tournamentmgmt.setup.phases.api.Phase;
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.CourtAllocationPolicy;
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.MatchDurationPolicy;
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.ScoringRules;
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.SeedingPolicy;
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.TieBreakRules;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public interface ConfigurationService {

	// Lifecycle
	Tournament createDraft(long organizerId, String name, Visibility visibility, long actingUserId);

	Tournament updateDraft(long tournamentId, Tournament draftChanges, long version, long actingUserId);

	Tournament publish(long tournamentId, long version, long actingUserId);

	Tournament openRegistration(long tournamentId, long version, long actingUserId);

	Tournament closeRegistration(long tournamentId, long version, long actingUserId);

	Tournament lockConfiguration(long tournamentId, long version, long actingUserId);

	Tournament start(long tournamentId, long version, long actingUserId);

	Tournament complete(long tournamentId, long version, long actingUserId);

	Tournament cancel(long tournamentId, long version, String reason, long actingUserId);

	// Core configuration
	Tournament setBasics(
		long tournamentId,
		String name,
		String description,
		Locale locale,
		Visibility visibility,
		long version,
		long actingUserId
	);

	Tournament setSchedule(
		long tournamentId,
		TimeWindow schedule,
		List<TimeWindow> registrationWindows,
		long version,
		long actingUserId
	);

	Tournament setVenueAndCourts(
		long tournamentId,
		Venue venue,
		List<Court> courts,
		long version,
		long actingUserId
	);

	Tournament setFormat(
		long tournamentId,
		TournamentFormat format,
		List<Category> categories,
		TeamSize teamSize,
		long version,
		long actingUserId
	);

	Tournament setCapacity(
		long tournamentId,
		Capacity capacity,
		long version,
		long actingUserId
	);

	Tournament setPolicies(
		long tournamentId,
		RegistrationPolicy registrationPolicy,
		SchedulingPolicy schedulingPolicy,
		CourtAllocationPolicy courtAllocationPolicy,
		long version,
		long actingUserId
	);

	Tournament setRules(
		long tournamentId,
		ScoringRules scoringRules,
		TieBreakRules tieBreakRules,
		MatchDurationPolicy matchDurationPolicy,
		SeedingPolicy seedingPolicy,
		long version,
		long actingUserId
	);

	// Roster
	Tournament setParticipantsRoster(
		long tournamentId,
		ParticipantsRoster roster,
		long version,
		long actingUserId
	);

	Tournament setCategoryRoster(
		long tournamentId,
		Category category,
		ParticipantsRoster roster,
		long version,
		long actingUserId
	);

	Tournament addParticipant(
		long tournamentId,
		Long playerId,
		Long teamId,
		Category category,
		long version,
		long actingUserId
	);

	Tournament removeParticipant(
		long tournamentId,
		Long playerId,
		Long teamId,
		Category category,
		long version,
		long actingUserId
	);

	// Phases & validation
	Tournament definePhases(
		long tournamentId,
		List<Phase> phases,
		long version,
		long actingUserId
	);

	void validateConfiguration(long tournamentId);

	// Queries
	Tournament get(long tournamentId);

	List<Tournament> listByOrganizer(
		long organizerId,
		Set<TournamentStatus> statuses,
		Visibility visibilityFilter
	);

	List<Tournament> findPublic(
		String search,
		Locale locale,
		Instant from,
		Instant to
	);
}
