package au.jefrin.club.dto;

import au.jefrin.user.dto.UserResponse;
import lombok.Builder;
import lombok.Getter;
import java.sql.Timestamp;

@Getter
@Builder
public class ClubResponse {
    private Long id;
    private String name;
    private String description;
    private Timestamp dateOfCreation;
    private UserResponse createdBy;
    private int memberCount;
    private Boolean isMember;
}
