package au.jefrin.club.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardPostPayload {
    private Long clubId;
    private Long postId;
    private String title;
    private String description;

    public DashboardPostPayload() {}
}

