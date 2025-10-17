package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity.TournamentEntity;

public interface TournamentRepository extends JpaRepository<TournamentEntity, Long> {}
