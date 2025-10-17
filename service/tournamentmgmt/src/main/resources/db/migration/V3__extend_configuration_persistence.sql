ALTER TABLE tournamentmgmt.tournaments
    ADD COLUMN IF NOT EXISTS venue_street VARCHAR(255),
    ADD COLUMN IF NOT EXISTS venue_postal_code VARCHAR(5),
    ADD COLUMN IF NOT EXISTS venue_city VARCHAR(255),
    ADD COLUMN IF NOT EXISTS venue_capacity_amount INTEGER,
    ADD COLUMN IF NOT EXISTS venue_capacity_unit VARCHAR(20);

ALTER TABLE tournamentmgmt.tournament_courts
    ADD COLUMN IF NOT EXISTS availability VARCHAR(32) NOT NULL DEFAULT 'AVAILABLE',
    ADD COLUMN IF NOT EXISTS type VARCHAR(32) NOT NULL DEFAULT 'STANDARD';

CREATE TABLE IF NOT EXISTS tournamentmgmt.tournament_disciplines (
    id BIGSERIAL PRIMARY KEY,
    tournament_id BIGINT NOT NULL REFERENCES tournamentmgmt.tournaments(id) ON DELETE CASCADE,
    discipline_id BIGINT NOT NULL,
    category tournamentmgmt.category NOT NULL,
    display_name VARCHAR(200) NOT NULL,
    team_size tournamentmgmt.team_size NOT NULL,
    CONSTRAINT uq_tournament_discipline UNIQUE (tournament_id, discipline_id)
);

CREATE TABLE IF NOT EXISTS tournamentmgmt.tournament_brackets (
    id BIGSERIAL PRIMARY KEY,
    tournament_discipline_id BIGINT NOT NULL REFERENCES tournamentmgmt.tournament_disciplines(id) ON DELETE CASCADE,
    bracket_id VARCHAR(100) NOT NULL,
    display_name VARCHAR(200) NOT NULL,
    format tournamentmgmt.tournament_format NOT NULL,
    capacity_amount INTEGER,
    capacity_unit VARCHAR(20),
    CONSTRAINT uq_tournament_bracket UNIQUE (tournament_discipline_id, bracket_id)
);
