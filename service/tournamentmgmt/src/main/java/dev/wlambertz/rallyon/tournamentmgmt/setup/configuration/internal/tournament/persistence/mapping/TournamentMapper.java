package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.mapping;

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.*;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity.TournamentEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Component
public class TournamentMapper {

    private final TournamentEntityMapper tournamentEntityMapper;

    public TournamentMapper(TournamentEntityMapper tournamentEntityMapper) {
        this.tournamentEntityMapper = tournamentEntityMapper;
    }

    public TournamentEntity toEntityForCreate(long organizerId, TournamentName name, Visibility visibility, long actingUserId, Instant now) {
        TournamentEntity e = new TournamentEntity();
        e.setOrganizerId(organizerId);
        e.setVisibility(visibility);
        e.setName(name.value());
        e.setStatus(TournamentStatus.DRAFT);
        e.setCreatedAt(now);
        e.setCreatedByUserId(actingUserId);
        e.setLastModifiedAt(now);
        e.setLastModifiedByUserId(actingUserId);
        e.setVersion(0L);
        return e;
    }

    public Tournament toApi(TournamentEntity entity) {
        TournamentFlat flat = tournamentEntityMapper.toFlat(entity);
        return Tournament.builder()
                .id(flat.id())
                .version(flat.version())
                .organizerId(flat.organizerId())
                .visibility(flat.visibility())
                .name(new TournamentName(flat.name()))
                .registrationWindows(List.of())
                .courts(List.of())
                .categories(List.of())
                .phases(List.of())
                .participants(new ParticipantsRoster(List.of(), List.of()))
                .categoryRosters(Map.of())
                .status(flat.status())
                .createdAt(flat.createdAt())
                .createdByUserId(flat.createdByUserId())
                .lastModifiedAt(flat.lastModifiedAt())
                .lastModifiedByUserId(flat.lastModifiedByUserId())
                .build();
    }
}

