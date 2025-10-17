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
@Table(name = "tournament_registration_windows", schema = "tournamentmgmt")
@Getter
@Setter
public class RegistrationWindowEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", nullable = false)
    private TournamentEntity tournament;

    @Column(name = "window_index", nullable = false)
    private short windowIndex;

    @Column(name = "registration_starts_at", nullable = false)
    private Instant registrationStartsAt;

    @Column(name = "registration_ends_at", nullable = false)
    private Instant registrationEndsAt;
}
