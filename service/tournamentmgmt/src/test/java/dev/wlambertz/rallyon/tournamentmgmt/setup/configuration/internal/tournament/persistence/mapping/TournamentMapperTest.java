package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.mapping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.BracketId;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Capacity;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Category;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Court;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.DisciplineConfig;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.ParticipantsRoster;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.RegistrationPolicy;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.SchedulingPolicy;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TeamSize;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TimeWindow;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Tournament;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TournamentFormat;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TournamentStatus;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Visibility;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Venue;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity.BracketEntity;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity.BracketParticipantEntity;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity.CourtEntity;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity.DisciplineEntity;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity.TournamentEntity;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity.ParticipantEntity;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity.RegistrationWindowEntity;
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.CourtAllocationPolicy;
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.MatchDurationPolicy;
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.ScoringRules;
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.SeedingPolicy;
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.TieBreakRules;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import org.junit.jupiter.api.Test;

class TournamentMapperTest {

    private final TournamentMapper mapper = new TournamentMapper();

    @Test
    void toApiProjectsExtendedConfiguration() {
        Instant now = Instant.parse("2025-10-17T10:00:00Z");

        TournamentEntity entity = new TournamentEntity();
        entity.setId(99L);
        entity.setVersion(3L);
        entity.setOrganizerId(77L);
        entity.setVisibility(Visibility.PUBLIC);
        entity.setName("RallyOn Masters");
        entity.setDescription("Season highlight");
        entity.setLocale("de-DE");
        entity.setScheduleStart(now);
        entity.setScheduleEnd(now.plusSeconds(7200));
        entity.setVenueName("Olympic Arena");
        entity.setVenueStreet("Main Street 1");
        entity.setVenuePostalCode("12345");
        entity.setVenueCity("Berlin");
        entity.setVenueCapacityAmount(5000);
        entity.setVenueCapacityUnit(Capacity.Unit.PEOPLE);
        entity.setCapacityMaxParticipants(256);
        entity.setRegistrationPolicy(RegistrationPolicy.OPEN);
        entity.setSchedulingPolicy(SchedulingPolicy.MAX_PARALLEL_MATCHES);
        entity.setCourtAllocationPolicy(CourtAllocationPolicy.SEQUENTIAL);
        entity.setSeedingPolicy(SeedingPolicy.MANUAL);
        entity.setScoringPointsPerGame(21);
        entity.setScoringGamesPerMatch(3);
        entity.setScoringWinByTwo(true);
        entity.setScoringCapPoints(30);
        entity.setTieBreakUseSetDifference(true);
        entity.setTieBreakUsePointsRatio(false);
        entity.setTieBreakUseBuchholz(false);
        entity.setMatchDurationPolicy(MatchDurationPolicy.FIXED_TIMEBOX);
        entity.setStatus(TournamentStatus.DRAFT);
        entity.setCreatedAt(now);
        entity.setCreatedByUserId(5L);
        entity.setLastModifiedAt(now);
        entity.setLastModifiedByUserId(5L);

        RegistrationWindowEntity registrationWindow = new RegistrationWindowEntity();
        registrationWindow.setTournament(entity);
        registrationWindow.setWindowIndex((short) 0);
        registrationWindow.setRegistrationStartsAt(now.minusSeconds(604800));
        registrationWindow.setRegistrationEndsAt(now.minusSeconds(86400));
        entity.getRegistrationWindows().add(registrationWindow);

        CourtEntity court = new CourtEntity();
        court.setTournament(entity);
        court.setId(201L);
        court.setLabel("Court A");
        court.setAvailability(Court.Availability.AVAILABLE);
        court.setType(Court.Type.STANDARD);
        court.setSortOrder((short) 1);
        entity.getCourts().add(court);

        DisciplineEntity discipline = new DisciplineEntity();
        discipline.setTournament(entity);
        discipline.setDisciplineId(11L);
        discipline.setCategory(Category.SINGLES);
        discipline.setDisplayName("Singles");
        discipline.setTeamSize(TeamSize.SINGLES);

        BracketEntity bracket = new BracketEntity();
        bracket.setDiscipline(discipline);
        bracket.setBracketId("main");
        bracket.setDisplayName("Main Draw");
        bracket.setFormat(TournamentFormat.KO_POULE);
        bracket.setCapacityAmount(128);
        bracket.setCapacityUnit(Capacity.Unit.PARTICIPANTS);
        discipline.getBrackets().add(bracket);

        BracketParticipantEntity bracketParticipant = new BracketParticipantEntity();
        bracketParticipant.setBracket(bracket);
        bracketParticipant.setPlayerId(2001L);
        bracketParticipant.setAddedAt(now.minusSeconds(300));
        bracketParticipant.setAddedByUserId(5L);
        bracket.getParticipants().add(bracketParticipant);
        entity.getDisciplines().add(discipline);

        ParticipantEntity participant = new ParticipantEntity();
        participant.setTournament(entity);
        participant.setPlayerId(1001L);
        participant.setAddedAt(now.minusSeconds(600));
        participant.setAddedByUserId(5L);
        entity.getParticipants().add(participant);

        Tournament tournament = mapper.toApi(entity);

        assertEquals(99L, tournament.id());
        assertEquals(3L, tournament.version());
        assertEquals("RallyOn Masters", tournament.name());
        assertEquals("Season highlight", tournament.description());
        assertEquals(Locale.GERMANY.getLanguage(), tournament.locale().getLanguage());

        TimeWindow schedule = tournament.schedule();
        assertNotNull(schedule);
        assertEquals(now, schedule.start());
        assertEquals(now.plusSeconds(7200), schedule.end());

        Venue venue = tournament.venue();
        assertNotNull(venue);
        assertEquals("Olympic Arena", venue.name());
        assertNotNull(venue.address());
        assertEquals("Berlin", venue.address().city());
        assertEquals(5000, venue.peopleCapacity().amount());

        assertEquals(1, tournament.registrationWindows().size());
        assertEquals(1, tournament.courts().size());
        Court mappedCourt = tournament.courts().get(0);
        assertEquals("Court A", mappedCourt.label());
        assertEquals(Court.Type.STANDARD, mappedCourt.type());

        List<DisciplineConfig> mappedDisciplines = tournament.disciplines();
        assertEquals(1, mappedDisciplines.size());
        DisciplineConfig mappedDiscipline = mappedDisciplines.get(0);
        assertEquals(11L, mappedDiscipline.id());
        assertEquals(1, mappedDiscipline.brackets().size());
        assertEquals("Main Draw", mappedDiscipline.brackets().get(0).displayName());

        assertEquals(256, tournament.capacity().amount());
        assertEquals(RegistrationPolicy.OPEN, tournament.registrationPolicy());
        assertEquals(SeedingPolicy.MANUAL, tournament.seedingPolicy());
        assertEquals(MatchDurationPolicy.FIXED_TIMEBOX, tournament.matchDurationPolicy());

        ScoringRules scoringRules = tournament.scoringRules();
        assertNotNull(scoringRules);
        assertEquals(21, scoringRules.pointsPerGame());
        assertEquals(ScoringRules.Type.TWO_BY_TWENTY_ONE, scoringRules.type());

        TieBreakRules tieBreakRules = tournament.tieBreakRules();
        assertNotNull(tieBreakRules);
        assertEquals(TieBreakRules.Type.HEAD_TO_HEAD, tieBreakRules.type());

        assertTrue(tournament.participants().playerIds().contains(1001L));

        Map<BracketId, ParticipantsRoster> bracketRosters = tournament.bracketRosters();
        assertEquals(1, bracketRosters.size());
        ParticipantsRoster roster = bracketRosters.get(new BracketId("main"));
        assertNotNull(roster);
        assertTrue(roster.playerIds().contains(2001L));
    }
}
