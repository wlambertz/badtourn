# Tournament Configuration: Disciplines & Brackets

## Overview
- Each tournament now owns a collection of `DisciplineConfig` records instead of a top-level format and category list.
- A discipline represents the event variant (e.g., Mixed Doubles) and declares shared defaults such as team size and policies.
- Disciplines contain one or more `BracketConfig` entries (German: Teilnehmerfeld/Feld). Brackets define skill tiers or open divisions, each with an independent tournament format and capacity.
- Participant operations, rosters, and registration now identify the target discipline and bracket explicitly.

## Domain Types
- `DisciplineConfig`  
  - `long id`: Stable numeric identifier assigned by the organizer (e.g., `101`).  
  - `Category category`: Singles, Doubles, or Mixed classification used for scheduling logic.  
  - `String displayName`: UI label ("Mixed Doubles").  
  - `TeamSize teamSize`: Team composition for the discipline.  
  - `List<BracketConfig> brackets`: Child brackets representing skill tiers or open fields.
- `BracketConfig`  
  - `BracketId id`: Identifier like `a-bracket` or `open`.  
  - `String displayName`: Presentation label (“A Bracket – Competitive”).  
  - `TournamentFormat format`: Game-play structure assigned to this bracket (Swiss, KO, etc.).  
  - `Capacity capacity`: Optional limits overriding the tournament-wide capacity.
- `Tournament.bracketRosters`: Map keyed by `BracketId` holding discipline-specific participant rosters. Global `Tournament.participants` remains for aggregated reads.

## Sample API Payload
```json
{
  "disciplines": [
    {
      "id": 101,
      "category": "MIXED",
      "displayName": "Mixed Doubles",
      "teamSize": {
        "minPlayers": 2,
        "maxPlayers": 2
      },
      "brackets": [
        {
          "id": "a-bracket",
          "displayName": "A Bracket – Competitive",
          "format": "SWISS",
          "capacity": {
            "amount": 32,
            "unit": "PEOPLE"
          }
        },
        {
          "id": "c-bracket",
          "displayName": "C Bracket – Hobby",
          "format": "ROUND_ROBIN"
        }
      ]
    }
  ]
}
```

## REST Contract Changes
- `PUT /api/tournamentmgmt/config/{tournamentId}/disciplines` replaces the former `/format` endpoint. Payload contains the full list of `DisciplineConfig` objects.
- Participant mutations (`POST|DELETE /participants`) now require `disciplineId` and `bracketId` values.
- Roster management: `PUT /participants/brackets/{bracketId}` addresses bracket-specific rosters. The legacy `category` based endpoints are removed.
- Serialized `Tournament` responses include the `disciplines` list and `bracketRosters` map alongside tournament-wide fields.

## Validation Rules
- Every discipline must include at least one bracket.  
- Discipline ids must be positive and unique within their tournament; `BracketId` values remain non-blank and unique within their discipline.  
- Bracket capacities and formats are validated independently; missing capacity implies the tournament-wide default applies.

## Migration Guidance
1. Existing tournaments migrate to a single discipline whose `TeamSize` mirrors the previous top-level value and whose brackets contain one entry reflecting the legacy format.  
2. Update client integrations to populate `disciplineId` and `bracketId` in roster and registration calls.  
3. Update persistence schema with new `tournament_discipline` and `tournament_bracket` tables (or equivalent) and backfill data before deploying code that expects the new shape.  
4. Coordinate API versioning or communicate the contract change to consumers; older clients must be updated before the new model is enforced.
