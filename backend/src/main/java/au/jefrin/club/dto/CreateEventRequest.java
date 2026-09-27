package au.jefrin.club.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateEventRequest {
    private Long clubId;
    private String name;
    private String description;
    private String venue;
    private LocalDateTime eventTime;
    private Integer capacity;
}
