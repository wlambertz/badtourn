package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.usecase;

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Tournament;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TournamentName;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Visibility;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity.TournamentEntity;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.mapping.TournamentMapper;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.repository.TournamentRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Objects;

@Component
class CreateDraftService implements CreateDraftUseCase {

    private final TournamentRepository tournamentRepository;
    private final TournamentMapper tournamentMapper;

    CreateDraftService(TournamentRepository tournamentRepository, TournamentMapper tournamentMapper) {
        this.tournamentRepository = tournamentRepository;
        this.tournamentMapper = tournamentMapper;
    }

    @Override
    @Transactional
    public Tournament execute(long organizerId, TournamentName name, Visibility visibility, long actingUserId) {
        Objects.requireNonNull(name, "Tournament name must not be null");
        Objects.requireNonNull(visibility, "Visibility must not be null");

        Instant now = Instant.now();
        TournamentEntity entity = tournamentMapper.toEntityForCreate(organizerId, name, visibility, actingUserId, now);
        return tournamentMapper.toApi(tournamentRepository.save(entity));
    }
}


