package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.mapping;

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.BracketConfig;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.BracketId;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Capacity;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Court;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.DisciplineConfig;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.RegistrationPolicy;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.SchedulingPolicy;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TimeWindow;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Tournament;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TournamentFormat;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TournamentStatus;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TeamSize;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Visibility;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.ParticipantsRoster;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Venue;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity.BracketEntity;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity.BracketParticipantEntity;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity.TournamentEntity;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity.ParticipantEntity;
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.CourtAllocationPolicy;
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.MatchDurationPolicy;
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.ScoringRules;
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.SeedingPolicy;
import dev.wlambertz.rallyon.tournamentmgmt.setup.rules.api.TieBreakRules;
import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class TournamentMapper {

    public TournamentEntity toEntityForCreate(
            long organizerId, String name, Visibility visibility, long actingUserId, Instant now) {
        TournamentEntity entity = new TournamentEntity();
        entity.setOrganizerId(organizerId);
        entity.setVisibility(visibility);
        entity.setName(name);
        entity.setStatus(TournamentStatus.DRAFT);
        entity.setCreatedAt(now);
        entity.setCreatedByUserId(actingUserId);
        entity.setLastModifiedAt(now);
        entity.setLastModifiedByUserId(actingUserId);
        entity.setVersion(0L);
        return entity;
    }

    public Tournament toApi(TournamentEntity entity) {
        return Tournament.builder()
                .id(entity.getId())
                .version(entity.getVersion())
                .organizerId(entity.getOrganizerId())
                .visibility(entity.getVisibility())
                .name(entity.getName())
                .description(entity.getDescription())
                .locale(toLocale(entity.getLocale()))
                .schedule(toSchedule(entity))
                .registrationWindows(toRegistrationWindows(entity))
                .venue(toVenue(entity))
                .courts(toCourts(entity))
                .disciplines(toDisciplines(entity))
                .capacity(toTournamentCapacity(entity))
                .registrationPolicy(entity.getRegistrationPolicy())
                .seedingPolicy(entity.getSeedingPolicy())
                .scoringRules(toScoringRules(entity))
                .tieBreakRules(toTieBreakRules(entity))
                .matchDurationPolicy(entity.getMatchDurationPolicy())
                .phases(List.of())
                .schedulingPolicy(entity.getSchedulingPolicy())
                .courtAllocationPolicy(entity.getCourtAllocationPolicy())
                .participants(toParticipantsRoster(entity.getParticipants()))
                .bracketRosters(toBracketRosters(entity))
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .createdByUserId(entity.getCreatedByUserId())
                .lastModifiedAt(entity.getLastModifiedAt())
                .lastModifiedByUserId(entity.getLastModifiedByUserId())
                .build();
    }

    private Locale toLocale(String localeValue) {
        if (localeValue == null || localeValue.isBlank()) {
            return null;
        }
        String normalized = localeValue.replace('_', '-');
        return Locale.forLanguageTag(normalized);
    }

    private TimeWindow toSchedule(TournamentEntity entity) {
        if (entity.getScheduleStart() == null || entity.getScheduleEnd() == null) {
            return null;
        }
        return new TimeWindow(entity.getScheduleStart(), entity.getScheduleEnd());
    }

    private List<TimeWindow> toRegistrationWindows(TournamentEntity entity) {
        return entity.getRegistrationWindows().stream()
                .sorted(Comparator.comparingInt(window -> window.getWindowIndex()))
                .map(window -> new TimeWindow(window.getRegistrationStartsAt(), window.getRegistrationEndsAt()))
                .toList();
    }

    private Venue toVenue(TournamentEntity entity) {
        String name = entity.getVenueName();
        String street = entity.getVenueStreet();
        String postalCode = entity.getVenuePostalCode();
        String city = entity.getVenueCity();
        Capacity venueCapacity = toCapacity(entity.getVenueCapacityAmount(), entity.getVenueCapacityUnit());

        Venue.Address address = null;
        if (street != null || postalCode != null || city != null) {
            address = new Venue.Address(street, postalCode, city);
        }

        if (name == null && address == null && venueCapacity == null) {
            return null;
        }

        return new Venue(name, address, venueCapacity);
    }

    private List<Court> toCourts(TournamentEntity entity) {
        return entity.getCourts().stream()
                .sorted(Comparator.comparingInt(court -> court.getSortOrder()))
                .map(court -> new Court(
                        court.getId(),
                        court.getLabel(),
                        court.getAvailability(),
                        court.getType()))
                .toList();
    }

    private List<DisciplineConfig> toDisciplines(TournamentEntity entity) {
        return entity.getDisciplines().stream()
                .map(discipline -> new DisciplineConfig(
                        discipline.getDisciplineId(),
                        discipline.getCategory(),
                        discipline.getDisplayName(),
                        discipline.getTeamSize(),
                        discipline.getBrackets().stream()
                                .map(this::toBracketConfig)
                                .toList()))
                .toList();
    }

    private BracketConfig toBracketConfig(BracketEntity bracket) {
        return new BracketConfig(
                new BracketId(bracket.getBracketId()),
                bracket.getDisplayName(),
                bracket.getFormat(),
                toCapacity(bracket.getCapacityAmount(), bracket.getCapacityUnit()));
    }

    private Capacity toTournamentCapacity(TournamentEntity entity) {
        Integer amount = entity.getCapacityMaxParticipants();
        if (amount == null) {
            return null;
        }
        return new Capacity(amount, Capacity.Unit.PARTICIPANTS);
    }

    private Capacity toCapacity(Integer amount, Capacity.Unit unit) {
        if (amount == null && unit == null) {
            return null;
        }
        Capacity.Unit resolvedUnit = unit;
        if (resolvedUnit == null && amount != null) {
            resolvedUnit = Capacity.Unit.PARTICIPANTS;
        }
        return new Capacity(amount, resolvedUnit);
    }

    private ScoringRules toScoringRules(TournamentEntity entity) {
        Integer points = entity.getScoringPointsPerGame();
        Integer games = entity.getScoringGamesPerMatch();
        Boolean winByTwo = entity.getScoringWinByTwo();
        Integer cap = entity.getScoringCapPoints();
        if (points == null || games == null || winByTwo == null) {
            return null;
        }
        boolean win = Boolean.TRUE.equals(winByTwo);
        ScoringRules candidate = ScoringRules.custom(points, games, win, cap);
        if (matches(candidate, ScoringRules.twoByTwentyOne())) {
            return ScoringRules.twoByTwentyOne();
        }
        if (matches(candidate, ScoringRules.threeByFifteen())) {
            return ScoringRules.threeByFifteen();
        }
        return candidate;
    }

    private boolean matches(ScoringRules candidate, ScoringRules preset) {
        return candidate.pointsPerGame() == preset.pointsPerGame()
                && candidate.gamesPerMatch() == preset.gamesPerMatch()
                && candidate.winByTwo() == preset.winByTwo()
                && Objects.equals(candidate.capPoints(), preset.capPoints());
    }

    private TieBreakRules toTieBreakRules(TournamentEntity entity) {
        Boolean setDifference = entity.getTieBreakUseSetDifference();
        Boolean pointsRatio = entity.getTieBreakUsePointsRatio();
        Boolean buchholz = entity.getTieBreakUseBuchholz();
        if (setDifference == null || pointsRatio == null || buchholz == null) {
            return null;
        }
        TieBreakRules candidate = TieBreakRules.custom(
                Boolean.TRUE.equals(setDifference),
                Boolean.TRUE.equals(pointsRatio),
                Boolean.TRUE.equals(buchholz));
        if (matches(candidate, TieBreakRules.headToHead())) {
            return TieBreakRules.headToHead();
        }
        if (matches(candidate, TieBreakRules.pointsRatio())) {
            return TieBreakRules.pointsRatio();
        }
        if (matches(candidate, TieBreakRules.swissStrength())) {
            return TieBreakRules.swissStrength();
        }
        return candidate;
    }

    private boolean matches(TieBreakRules candidate, TieBreakRules preset) {
        return candidate.useSetDifference() == preset.useSetDifference()
                && candidate.usePointsRatio() == preset.usePointsRatio()
                && candidate.useBuchholz() == preset.useBuchholz();
    }

    private ParticipantsRoster toParticipantsRoster(List<ParticipantEntity> participantEntities) {
        List<Long> playerIds = participantEntities.stream()
                .filter(participant -> participant.getCategory() == null && participant.getPlayerId() != null)
                .map(ParticipantEntity::getPlayerId)
                .toList();

        List<Long> teamIds = participantEntities.stream()
                .filter(participant -> participant.getCategory() == null && participant.getTeamId() != null)
                .map(ParticipantEntity::getTeamId)
                .toList();

        return new ParticipantsRoster(playerIds, teamIds);
    }

    private Map<BracketId, ParticipantsRoster> toBracketRosters(TournamentEntity entity) {
        return entity.getDisciplines().stream()
                .flatMap(discipline -> discipline.getBrackets().stream())
                .collect(Collectors.toMap(
                        bracket -> new BracketId(bracket.getBracketId()),
                        this::toBracketRoster,
                        (existing, replacement) -> replacement,
                        LinkedHashMap::new));
    }

    private ParticipantsRoster toBracketRoster(BracketEntity bracket) {
        List<Long> playerIds = bracket.getParticipants().stream()
                .map(BracketParticipantEntity::getPlayerId)
                .filter(Objects::nonNull)
                .toList();
        List<Long> teamIds = bracket.getParticipants().stream()
                .map(BracketParticipantEntity::getTeamId)
                .filter(Objects::nonNull)
                .toList();

        boolean hasPlayers = !playerIds.isEmpty();
        boolean hasTeams = !teamIds.isEmpty();

        if (hasPlayers && !hasTeams) {
            return new ParticipantsRoster(playerIds, null);
        }
        if (hasTeams && !hasPlayers) {
            return new ParticipantsRoster(null, teamIds);
        }
        if (!hasPlayers && !hasTeams) {
            boolean teamBased = bracket.getDiscipline() != null
                    && bracket.getDiscipline().getTeamSize() != null
                    && bracket.getDiscipline().getTeamSize() != TeamSize.SINGLES;
            return teamBased
                    ? new ParticipantsRoster(null, List.of())
                    : new ParticipantsRoster(List.of(), null);
        }
        boolean teamBased = bracket.getDiscipline() != null
                && bracket.getDiscipline().getTeamSize() != null
                && bracket.getDiscipline().getTeamSize() != TeamSize.SINGLES;
        return teamBased
                ? new ParticipantsRoster(null, teamIds)
                : new ParticipantsRoster(playerIds, null);
    }
}
