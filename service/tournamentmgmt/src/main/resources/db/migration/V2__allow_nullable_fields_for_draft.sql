ALTER TABLE tournamentmgmt.tournaments
  ALTER COLUMN description DROP NOT NULL,
  ALTER COLUMN locale DROP NOT NULL,
  ALTER COLUMN schedule_start DROP NOT NULL,
  ALTER COLUMN schedule_end DROP NOT NULL,
  ALTER COLUMN format DROP NOT NULL,
  ALTER COLUMN capacity_max_participants DROP NOT NULL,
  ALTER COLUMN team_size DROP NOT NULL,
  ALTER COLUMN registration_policy DROP NOT NULL,
  ALTER COLUMN scheduling_policy DROP NOT NULL,
  ALTER COLUMN court_allocation_policy DROP NOT NULL,
  ALTER COLUMN scoring_points_per_game DROP NOT NULL,
  ALTER COLUMN scoring_games_per_match DROP NOT NULL,
  ALTER COLUMN scoring_win_by_two DROP NOT NULL,
  ALTER COLUMN tie_break_use_set_difference DROP NOT NULL,
  ALTER COLUMN tie_break_use_points_ratio DROP NOT NULL,
  ALTER COLUMN tie_break_use_buchholz DROP NOT NULL,
  ALTER COLUMN match_duration_policy DROP NOT NULL,
  ALTER COLUMN seeding_policy DROP NOT NULL;


