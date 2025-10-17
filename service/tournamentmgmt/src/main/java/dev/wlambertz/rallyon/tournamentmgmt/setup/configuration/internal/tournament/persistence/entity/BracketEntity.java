package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity;

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Capacity;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TournamentFormat;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tournament_brackets", schema = "tournamentmgmt")
@Getter
@Setter
public class BracketEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_discipline_id", nullable = false)
    private DisciplineEntity discipline;

    @Column(name = "bracket_id", nullable = false)
    private String bracketId;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(name = "format", nullable = false)
    private TournamentFormat format;

    @Column(name = "capacity_amount")
    private Integer capacityAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "capacity_unit")
    private Capacity.Unit capacityUnit;

    @OneToMany(mappedBy = "bracket", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BracketParticipantEntity> participants = new ArrayList<>();
}
