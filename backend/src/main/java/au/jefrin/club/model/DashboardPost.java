package au.jefrin.club.model;

import java.sql.Timestamp;

public class DashboardPost {
    private Long id;
    private Long clubId;
    private String title;
    private String description;
    private Long createdBy;
    private Long lastUpdatedBy;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public DashboardPost() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getClubId() { return clubId; }
    public void setClubId(Long clubId) { this.clubId = clubId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }

    public Long getLastUpdatedBy() { return lastUpdatedBy; }
    public void setLastUpdatedBy(Long lastUpdatedBy) { this.lastUpdatedBy = lastUpdatedBy; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}

