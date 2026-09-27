package au.jefrin.club.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ClubEventResponse {
    private Long id;
    private Long clubId;
    private String name;
    private String description;
    private String venue;
    private LocalDateTime eventTime;
    private int capacity;
    private int ticketsIssued;
    private boolean hasTicket;
}
