package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.usecase;

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Tournament;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TournamentName;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Visibility;

public interface CreateDraftUseCase {
    Tournament execute(long organizerId, TournamentName name, Visibility visibility, long actingUserId);
}


