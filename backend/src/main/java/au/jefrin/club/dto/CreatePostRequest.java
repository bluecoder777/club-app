package au.jefrin.club.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePostRequest {
    private Long clubId;
    private String title;
    private String description;

    public CreatePostRequest() {}
}

