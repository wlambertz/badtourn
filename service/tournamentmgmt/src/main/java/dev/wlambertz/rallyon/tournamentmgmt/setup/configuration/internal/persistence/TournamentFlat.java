package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.persistence;

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TournamentStatus;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Visibility;
import java.time.Instant;

public class TournamentFlat {
    private Long id;
    private Long version;
    private long organizerId;
    private Visibility visibility;
    private String name;
    private TournamentStatus status;
    private Instant createdAt;
    private long createdByUserId;
    private Instant lastModifiedAt;
    private long lastModifiedByUserId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
    public long getOrganizerId() { return organizerId; }
    public void setOrganizerId(long organizerId) { this.organizerId = organizerId; }
    public Visibility getVisibility() { return visibility; }
    public void setVisibility(Visibility visibility) { this.visibility = visibility; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public TournamentStatus getStatus() { return status; }
    public void setStatus(TournamentStatus status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public long getCreatedByUserId() { return createdByUserId; }
    public void setCreatedByUserId(long createdByUserId) { this.createdByUserId = createdByUserId; }
    public Instant getLastModifiedAt() { return lastModifiedAt; }
    public void setLastModifiedAt(Instant lastModifiedAt) { this.lastModifiedAt = lastModifiedAt; }
    public long getLastModifiedByUserId() { return lastModifiedByUserId; }
    public void setLastModifiedByUserId(long lastModifiedByUserId) { this.lastModifiedByUserId = lastModifiedByUserId; }
}


