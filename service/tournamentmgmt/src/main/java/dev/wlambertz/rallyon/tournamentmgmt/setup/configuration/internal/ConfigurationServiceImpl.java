package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal;

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Capacity;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Category;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.ConfigurationService;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Court;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.ParticipantsRoster;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.RegistrationPolicy;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.SchedulingPolicy;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TeamSize;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TimeWindow;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Tournament;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TournamentFormat;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TournamentName;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TournamentStatus;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Venue;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Visibility;
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
public class ConfigurationServiceImpl implements ConfigurationService {

    @Override
    public Tournament createDraft(long organizerId, TournamentName name, Visibility visibility, long actingUserId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Tournament updateDraft(long tournamentId, Tournament draftChanges, long version, long actingUserId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Tournament publish(long tournamentId, long version, long actingUserId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Tournament openRegistration(long tournamentId, long version, long actingUserId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Tournament closeRegistration(long tournamentId, long version, long actingUserId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Tournament lockConfiguration(long tournamentId, long version, long actingUserId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Tournament start(long tournamentId, long version, long actingUserId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Tournament complete(long tournamentId, long version, long actingUserId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Tournament cancel(long tournamentId, long version, String reason, long actingUserId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Tournament setBasics(long tournamentId, TournamentName name, String description, Locale locale, Visibility visibility, long version, long actingUserId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Tournament setSchedule(long tournamentId, TimeWindow schedule, List<TimeWindow> registrationWindows, long version, long actingUserId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Tournament setVenueAndCourts(long tournamentId, Venue venue, List<Court> courts, long version, long actingUserId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Tournament setFormat(long tournamentId, TournamentFormat format, List<Category> categories, TeamSize teamSize, long version, long actingUserId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Tournament setCapacity(long tournamentId, Capacity capacity, long version, long actingUserId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Tournament setPolicies(long tournamentId, RegistrationPolicy registrationPolicy, SchedulingPolicy schedulingPolicy, CourtAllocationPolicy courtAllocationPolicy, long version, long actingUserId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Tournament setRules(long tournamentId, ScoringRules scoringRules, TieBreakRules tieBreakRules, MatchDurationPolicy matchDurationPolicy, SeedingPolicy seedingPolicy, long version, long actingUserId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Tournament setParticipantsRoster(long tournamentId, ParticipantsRoster roster, long version, long actingUserId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Tournament setCategoryRoster(long tournamentId, Category category, ParticipantsRoster roster, long version, long actingUserId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Tournament addParticipant(long tournamentId, Long playerId, Long teamId, Category category, long version, long actingUserId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Tournament removeParticipant(long tournamentId, Long playerId, Long teamId, Category category, long version, long actingUserId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Tournament definePhases(long tournamentId, List<Phase> phases, long version, long actingUserId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void validateConfiguration(long tournamentId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Tournament get(long tournamentId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public List<Tournament> listByOrganizer(long organizerId, Set<TournamentStatus> statuses, Visibility visibilityFilter) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public List<Tournament> findPublic(String search, Locale locale, Instant from, Instant to) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
