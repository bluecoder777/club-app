package au.jefrin.club.dto;

import au.jefrin.user.dto.UserResponse;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import au.jefrin.club.model.Role;

@Getter
@Builder
public class ClubResponse {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime dateOfCreation;
    private UserResponse createdBy;
    private int memberCount;
    private Boolean isMember;
    private Role currentUserRole;
}
