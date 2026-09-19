package au.jefrin.club.model;

import java.sql.Timestamp;

public class Club {
    private Long id;
    private String name;
    private String description;
    private Timestamp dateOfCreation;
    private Long createdBy;
    private int memberCount;

    public Club() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Timestamp getDateOfCreation() { return dateOfCreation; }
    public void setDateOfCreation(Timestamp dateOfCreation) { this.dateOfCreation = dateOfCreation; }
    
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }

    public int getMemberCount() { return memberCount; }
    public void setMemberCount(int memberCount) { this.memberCount = memberCount; }
}
