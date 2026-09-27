package au.jefrin.club.repository;

import au.jefrin.club.dto.ClubResponse;
import au.jefrin.club.model.Club;
import au.jefrin.club.model.Member;

import java.util.List;
import java.util.Optional;

public interface ClubRepository {
    Club createWithFoundingMember(Club club, Member foundingMember);

    Optional<Club> findById(Long id);

    void update(Club club);

    Optional<ClubResponse> findClubResponseById(Long id, Long currentUserId);

    List<ClubResponse> findAllClubResponses(Long currentUserId);
}
