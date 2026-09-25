package au.jefrin.club.repository;

import au.jefrin.club.dto.ClubMemberResponse;
import au.jefrin.club.model.Member;
import au.jefrin.club.model.Role;

import java.sql.SQLException;
import java.util.List;

public interface MemberRepository {
    Member save(Member member) throws SQLException;

    boolean existsByUserAndClub(Long userId, Long clubId) throws SQLException;

    Member findByUserAndClub(Long userId, Long clubId) throws SQLException;

    List<ClubMemberResponse> findAllMembersByClubId(Long clubId) throws SQLException;

    void updateRole(Long userId, Long clubId, Role role) throws SQLException;

    void deleteByUserAndClub(Long userId, Long clubId) throws SQLException;
}
