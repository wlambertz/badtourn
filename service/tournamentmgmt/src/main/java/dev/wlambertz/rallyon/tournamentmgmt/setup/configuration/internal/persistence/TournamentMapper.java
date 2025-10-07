package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.persistence;

import com.googlecode.jmapper.JMapper;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.*;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Component
public class TournamentMapper {

    private final JMapper<TournamentFlat, TournamentEntity> toFlat;

    public TournamentMapper() {
        this.toFlat = new JMapper<>(TournamentFlat.class, TournamentEntity.class);
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
        TournamentFlat flat = toFlat.getDestination(entity);
        return Tournament.builder()
                .id(flat.getId())
                .version(flat.getVersion())
                .organizerId(flat.getOrganizerId())
                .visibility(flat.getVisibility())
                .name(new TournamentName(flat.getName()))
                .registrationWindows(List.of())
                .courts(List.of())
                .categories(List.of())
                .phases(List.of())
                .participants(new ParticipantsRoster(List.of(), List.of()))
                .categoryRosters(Map.of())
                .status(flat.getStatus())
                .createdAt(flat.getCreatedAt())
                .createdByUserId(flat.getCreatedByUserId())
                .lastModifiedAt(flat.getLastModifiedAt())
                .lastModifiedByUserId(flat.getLastModifiedByUserId())
                .build();
    }
}


