-- Schema for the RallyOn tournament management service
CREATE SCHEMA IF NOT EXISTS tournamentmgmt;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_type t
        JOIN pg_namespace n ON n.oid = t.typnamespace
        WHERE t.typname = 'visibility' AND n.nspname = 'tournamentmgmt'
    ) THEN
        CREATE TYPE tournamentmgmt.visibility AS ENUM ('PRIVATE', 'ORGANIZATION', 'PUBLIC');
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_type t
        JOIN pg_namespace n ON n.oid = t.typnamespace
        WHERE t.typname = 'tournament_format' AND n.nspname = 'tournamentmgmt'
    ) THEN
        CREATE TYPE tournamentmgmt.tournament_format AS ENUM (
            'KAISER',
            'SWISS',
            'KO_POULE',
            'KO_ABC',
            'ROUND_ROBIN',
            'KING_OF_THE_COURT',
            'LOTTERY',
            'RANKING'
        );
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_type t
        JOIN pg_namespace n ON n.oid = t.typnamespace
        WHERE t.typname = 'registration_policy' AND n.nspname = 'tournamentmgmt'
    ) THEN
        CREATE TYPE tournamentmgmt.registration_policy AS ENUM ('OPEN', 'INVITE_ONLY');
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_type t
        JOIN pg_namespace n ON n.oid = t.typnamespace
        WHERE t.typname = 'scheduling_policy' AND n.nspname = 'tournamentmgmt'
    ) THEN
        CREATE TYPE tournamentmgmt.scheduling_policy AS ENUM ('MAX_PARALLEL_MATCHES', 'ROUND_PACING');
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_type t
        JOIN pg_namespace n ON n.oid = t.typnamespace
        WHERE t.typname = 'court_allocation_policy' AND n.nspname = 'tournamentmgmt'
    ) THEN
        CREATE TYPE tournamentmgmt.court_allocation_policy AS ENUM ('SEQUENTIAL', 'FILL_LOWEST_NUMBER_FIRST');
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_type t
        JOIN pg_namespace n ON n.oid = t.typnamespace
        WHERE t.typname = 'team_size' AND n.nspname = 'tournamentmgmt'
    ) THEN
        CREATE TYPE tournamentmgmt.team_size AS ENUM ('SINGLES', 'DOUBLES');
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_type t
        JOIN pg_namespace n ON n.oid = t.typnamespace
        WHERE t.typname = 'category' AND n.nspname = 'tournamentmgmt'
    ) THEN
        CREATE TYPE tournamentmgmt.category AS ENUM ('SINGLES', 'DOUBLES', 'MIXED');
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_type t
        JOIN pg_namespace n ON n.oid = t.typnamespace
        WHERE t.typname = 'match_duration_policy' AND n.nspname = 'tournamentmgmt'
    ) THEN
        CREATE TYPE tournamentmgmt.match_duration_policy AS ENUM ('FIXED_TIMEBOX', 'BEST_OF_N_GAMES');
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_type t
        JOIN pg_namespace n ON n.oid = t.typnamespace
        WHERE t.typname = 'seeding_policy' AND n.nspname = 'tournamentmgmt'
    ) THEN
        CREATE TYPE tournamentmgmt.seeding_policy AS ENUM ('MANUAL', 'RATING_BASED', 'RANDOM');
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_type t
        JOIN pg_namespace n ON n.oid = t.typnamespace
        WHERE t.typname = 'tournament_status' AND n.nspname = 'tournamentmgmt'
    ) THEN
        CREATE TYPE tournamentmgmt.tournament_status AS ENUM (
            'DRAFT',
            'PUBLISHED',
            'REGISTRATION_OPEN',
            'LOCKED',
            'IN_PROGRESS',
            'COMPLETED',
            'CANCELED'
        );
    END IF;
END
$$;

CREATE TABLE IF NOT EXISTS tournamentmgmt.tournaments (
    id BIGSERIAL PRIMARY KEY,
    organizer_id BIGINT NOT NULL,
    visibility tournamentmgmt.visibility NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    locale VARCHAR(20) NOT NULL,
    schedule_start TIMESTAMPTZ NOT NULL,
    schedule_end TIMESTAMPTZ NOT NULL,
    venue_name VARCHAR(200),
    venue_address TEXT,
    format tournamentmgmt.tournament_format NOT NULL,
    capacity_max_participants INTEGER NOT NULL,
    team_size tournamentmgmt.team_size NOT NULL,
    registration_policy tournamentmgmt.registration_policy NOT NULL,
    scheduling_policy tournamentmgmt.scheduling_policy NOT NULL,
    court_allocation_policy tournamentmgmt.court_allocation_policy NOT NULL,
    scoring_points_per_game SMALLINT NOT NULL,
    scoring_games_per_match SMALLINT NOT NULL,
    scoring_win_by_two BOOLEAN NOT NULL,
    scoring_cap_points SMALLINT,
    tie_break_use_set_difference BOOLEAN NOT NULL,
    tie_break_use_points_ratio BOOLEAN NOT NULL,
    tie_break_use_buchholz BOOLEAN NOT NULL,
    match_duration_policy tournamentmgmt.match_duration_policy NOT NULL,
    seeding_policy tournamentmgmt.seeding_policy NOT NULL,
    status tournamentmgmt.tournament_status NOT NULL DEFAULT 'DRAFT',
    cancel_reason TEXT,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by_user_id BIGINT NOT NULL,
    last_modified_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    last_modified_by_user_id BIGINT NOT NULL,
    CONSTRAINT chk_schedule_range CHECK (schedule_end >= schedule_start),
    CONSTRAINT chk_capacity_positive CHECK (capacity_max_participants > 0),
    CONSTRAINT chk_scoring_points_positive CHECK (scoring_points_per_game > 0 AND scoring_games_per_match > 0),
    CONSTRAINT chk_scoring_cap_positive CHECK (scoring_cap_points IS NULL OR scoring_cap_points > 0)
);

CREATE INDEX IF NOT EXISTS idx_tournaments_organizer ON tournamentmgmt.tournaments (organizer_id);
CREATE INDEX IF NOT EXISTS idx_tournaments_visibility_status ON tournamentmgmt.tournaments (visibility, status);
CREATE INDEX IF NOT EXISTS idx_tournaments_schedule ON tournamentmgmt.tournaments (schedule_start, schedule_end);

CREATE TABLE IF NOT EXISTS tournamentmgmt.tournament_registration_windows (
    id BIGSERIAL PRIMARY KEY,
    tournament_id BIGINT NOT NULL REFERENCES tournamentmgmt.tournaments(id) ON DELETE CASCADE,
    window_index SMALLINT NOT NULL,
    registration_starts_at TIMESTAMPTZ NOT NULL,
    registration_ends_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT chk_registration_window_range CHECK (registration_ends_at >= registration_starts_at),
    CONSTRAINT uq_tournament_registration_window UNIQUE (tournament_id, window_index)
);

CREATE INDEX IF NOT EXISTS idx_registration_windows_tournament ON tournamentmgmt.tournament_registration_windows (tournament_id);

CREATE TABLE IF NOT EXISTS tournamentmgmt.tournament_courts (
    id BIGSERIAL PRIMARY KEY,
    tournament_id BIGINT NOT NULL REFERENCES tournamentmgmt.tournaments(id) ON DELETE CASCADE,
    source_court_id BIGINT,
    label VARCHAR(100) NOT NULL,
    sort_order SMALLINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_tournament_court_label UNIQUE (tournament_id, label),
    CONSTRAINT uq_tournament_court_source UNIQUE (tournament_id, source_court_id)
);

CREATE INDEX IF NOT EXISTS idx_tournament_courts_tournament ON tournamentmgmt.tournament_courts (tournament_id);

CREATE TABLE IF NOT EXISTS tournamentmgmt.tournament_categories (
    tournament_id BIGINT NOT NULL REFERENCES tournamentmgmt.tournaments(id) ON DELETE CASCADE,
    category tournamentmgmt.category NOT NULL,
    PRIMARY KEY (tournament_id, category)
);

CREATE TABLE IF NOT EXISTS tournamentmgmt.tournament_phases (
    id BIGSERIAL PRIMARY KEY,
    tournament_id BIGINT NOT NULL REFERENCES tournamentmgmt.tournaments(id) ON DELETE CASCADE,
    phase_order SMALLINT NOT NULL,
    phase_type VARCHAR(100) NOT NULL,
    configuration JSONB,
    CONSTRAINT uq_tournament_phase_order UNIQUE (tournament_id, phase_order)
);

CREATE INDEX IF NOT EXISTS idx_tournament_phases_tournament ON tournamentmgmt.tournament_phases (tournament_id);

CREATE TABLE IF NOT EXISTS tournamentmgmt.tournament_participants (
    id BIGSERIAL PRIMARY KEY,
    tournament_id BIGINT NOT NULL REFERENCES tournamentmgmt.tournaments(id) ON DELETE CASCADE,
    category tournamentmgmt.category,
    player_id BIGINT,
    team_id BIGINT,
    added_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    added_by_user_id BIGINT NOT NULL,
    CONSTRAINT chk_participant_reference CHECK (
        ((player_id IS NOT NULL)::int + (team_id IS NOT NULL)::int) = 1
    )
);

CREATE INDEX IF NOT EXISTS idx_participants_tournament ON tournamentmgmt.tournament_participants (tournament_id);
CREATE INDEX IF NOT EXISTS idx_participants_category ON tournamentmgmt.tournament_participants (category);
CREATE INDEX IF NOT EXISTS idx_participants_player ON tournamentmgmt.tournament_participants (player_id) WHERE player_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_participants_team ON tournamentmgmt.tournament_participants (team_id) WHERE team_id IS NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS ux_participants_player_general
    ON tournamentmgmt.tournament_participants (tournament_id, player_id)
    WHERE player_id IS NOT NULL AND category IS NULL;

CREATE UNIQUE INDEX IF NOT EXISTS ux_participants_team_general
    ON tournamentmgmt.tournament_participants (tournament_id, team_id)
    WHERE team_id IS NOT NULL AND category IS NULL;

CREATE UNIQUE INDEX IF NOT EXISTS ux_participants_player_category
    ON tournamentmgmt.tournament_participants (tournament_id, category, player_id)
    WHERE player_id IS NOT NULL AND category IS NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS ux_participants_team_category
    ON tournamentmgmt.tournament_participants (tournament_id, category, team_id)
    WHERE team_id IS NOT NULL AND category IS NOT NULL;

