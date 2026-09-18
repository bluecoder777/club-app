package au.jefrin.club.dto;

public class CreateClubRequest {
    private String name;
    private String description;

    public CreateClubRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
