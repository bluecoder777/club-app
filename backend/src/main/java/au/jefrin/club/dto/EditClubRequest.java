package au.jefrin.club.dto;

public class EditClubRequest {
    private Long clubId;
    private String name;
    private String description;

    public EditClubRequest() {}

    public Long getClubId() { return clubId; }
    public void setClubId(Long clubId) { this.clubId = clubId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
