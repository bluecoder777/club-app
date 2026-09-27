package au.jefrin.club.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import au.jefrin.user.dto.UserResponse;

@Getter
@Builder
public class DashboardPostResponse {
    private Long id;
    private Long clubId;
    private String title;
    private String description;
    private UserResponse createdBy;
    private UserResponse lastUpdatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

