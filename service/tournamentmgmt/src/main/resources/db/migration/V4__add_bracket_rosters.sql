CREATE TABLE IF NOT EXISTS tournamentmgmt.tournament_bracket_participants (
    id BIGSERIAL PRIMARY KEY,
    tournament_bracket_id BIGINT NOT NULL REFERENCES tournamentmgmt.tournament_brackets(id) ON DELETE CASCADE,
    player_id BIGINT,
    team_id BIGINT,
    added_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    added_by_user_id BIGINT NOT NULL,
    CONSTRAINT chk_bracket_participant_reference CHECK (
        ((player_id IS NOT NULL)::int + (team_id IS NOT NULL)::int) = 1
    )
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_bracket_participant_player
    ON tournamentmgmt.tournament_bracket_participants (tournament_bracket_id, player_id)
    WHERE player_id IS NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS ux_bracket_participant_team
    ON tournamentmgmt.tournament_bracket_participants (tournament_bracket_id, team_id)
    WHERE team_id IS NOT NULL;
