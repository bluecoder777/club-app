package au.jefrin.club.repository;

import au.jefrin.club.dto.ClubEventResponse;
import au.jefrin.club.model.ClubEvent;

import java.util.List;
import java.util.Optional;

public interface ClubEventRepository {
    ClubEvent save(ClubEvent event);

    Optional<ClubEvent> findById(Long eventId);

    Optional<ClubEventResponse> findResponseById(Long eventId, Long currentUserId);

    List<ClubEventResponse> findAllResponsesByClubId(Long clubId, Long currentUserId);
}
