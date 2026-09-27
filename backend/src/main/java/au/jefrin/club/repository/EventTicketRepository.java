package au.jefrin.club.repository;

import au.jefrin.club.model.TicketReservationResult;

public interface EventTicketRepository {
    TicketReservationResult reserve(Long eventId, Long userId);

    boolean cancel(Long eventId, Long userId);
}
