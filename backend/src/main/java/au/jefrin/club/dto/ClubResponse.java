package au.jefrin.club.dto;

import au.jefrin.user.dto.UserResponse;
import lombok.Builder;
import lombok.Getter;
import java.sql.Timestamp;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Builder
public class ClubResponse {
    private Long id;
    private String name;
    private String description;
    private Timestamp dateOfCreation;
    private UserResponse createdBy;
    private int memberCount;
    @JsonProperty("isMember")
    private boolean isMember;
}
