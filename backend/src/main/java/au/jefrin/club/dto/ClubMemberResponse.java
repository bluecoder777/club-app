package au.jefrin.club.dto;

import au.jefrin.club.model.Role;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ClubMemberResponse {
    private Long id;
    private Long userId;
    private String name;
    private String email;
    private Role role;
    private LocalDateTime joinedAt;
}

