package au.jefrin.club.repository;

import au.jefrin.club.dto.ClubMemberResponse;
import au.jefrin.club.model.Member;
import au.jefrin.club.model.Role;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {
    Member save(Member member);

    boolean existsByUserAndClub(Long userId, Long clubId);

    boolean hasMembers(Long clubId);

    Optional<Member> findByUserAndClub(Long userId, Long clubId);

    List<ClubMemberResponse> findAllMembersByClubId(Long clubId);

    void updateRole(Long userId, Long clubId, Role role);

    void handOverAdministrationAndRemove(Long departingUserId, Long successorUserId, Long clubId);

    void deleteByUserAndClub(Long userId, Long clubId);
}
