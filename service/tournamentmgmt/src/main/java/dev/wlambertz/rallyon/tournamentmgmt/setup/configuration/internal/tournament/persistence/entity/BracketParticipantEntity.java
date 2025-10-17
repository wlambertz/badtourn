package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tournament_bracket_participants", schema = "tournamentmgmt")
@Getter
@Setter
public class BracketParticipantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_bracket_id", nullable = false)
    private BracketEntity bracket;

    @Column(name = "player_id")
    private Long playerId;

    @Column(name = "team_id")
    private Long teamId;

    @Column(name = "added_at", nullable = false)
    private Instant addedAt;

    @Column(name = "added_by_user_id", nullable = false)
    private long addedByUserId;
}
