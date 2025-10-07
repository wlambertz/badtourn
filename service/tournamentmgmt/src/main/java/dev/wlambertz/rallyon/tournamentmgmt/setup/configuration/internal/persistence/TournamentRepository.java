package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TournamentRepository extends JpaRepository<TournamentEntity, Long> {}


