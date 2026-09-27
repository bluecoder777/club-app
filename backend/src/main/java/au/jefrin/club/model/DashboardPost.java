package au.jefrin.club.model;

import java.time.LocalDateTime;

public class DashboardPost {
    private Long id;
    private Long clubId;
    private String title;
    private String description;
    private Long createdBy;
    private Long lastUpdatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

