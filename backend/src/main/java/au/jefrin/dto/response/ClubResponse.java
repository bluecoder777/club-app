package au.jefrin.dto.response;

import au.jefrin.model.Club;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClubResponse {
    private Long id;
    private String name;
    private String description;
    private Long createdBy;
    private String dateOfCreation;
    
    public static ClubResponse fromClub(Club club) {
        return ClubResponse.builder()
                .id(club.getId())
                .name(club.getName())
                .description(club.getDescription())
                .createdBy(club.getCreatedBy())
                .dateOfCreation(club.getDateOfCreation().toString())
                .build();
    }
}

