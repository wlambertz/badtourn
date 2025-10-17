package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity;

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Court;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tournament_courts", schema = "tournamentmgmt")
@Getter
@Setter
public class CourtEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", nullable = false)
    private TournamentEntity tournament;

    @Column(name = "source_court_id")
    private Long sourceCourtId;

    @Column(name = "label", nullable = false)
    private String label;

    @Column(name = "sort_order", nullable = false)
    private short sortOrder;

    @Enumerated(EnumType.STRING)
    @Column(name = "availability", nullable = false)
    private Court.Availability availability;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private Court.Type type;
}
