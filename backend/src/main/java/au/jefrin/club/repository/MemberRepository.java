package au.jefrin.club.repository;

import au.jefrin.club.model.Role;

import au.jefrin.common.config.DatabaseConfig;
import au.jefrin.club.model.Member;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import au.jefrin.club.dto.ClubMemberResponse;

public class MemberRepository {

    public Member save(Member member) throws SQLException {
        String query = "INSERT INTO member (user_id, club_id, role) VALUES (?, ?, ?::member_role) RETURNING id, joined_at";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setLong(1, member.getUserId());
            stmt.setLong(2, member.getClubId());
            stmt.setString(3, member.getRole().name());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    member.setId(rs.getLong("id"));
                    member.setJoinedAt(rs.getTimestamp("joined_at"));
                    return member;
                }
            }
        }
        throw new SQLException("Failed to add member");
    }

    public boolean existsByUserAndClub(Long userId, Long clubId) throws SQLException {
        String query = "SELECT 1 FROM member WHERE user_id = ? AND club_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, userId);
            stmt.setLong(2, clubId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }


    public Member findByUserAndClub(Long userId, Long clubId) throws SQLException {
        String query = "SELECT id, user_id, club_id, role, joined_at FROM member WHERE user_id = ? AND club_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, userId);
            stmt.setLong(2, clubId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Member m = new Member();
                    m.setId(rs.getLong("id"));
                    m.setUserId(rs.getLong("user_id"));
                    m.setClubId(rs.getLong("club_id"));
                    m.setRole(Role.valueOf(rs.getString("role")));
                    m.setJoinedAt(rs.getTimestamp("joined_at"));
                    return m;
                }
            }
        }
        return null;
    }

    public List<ClubMemberResponse> findAllMembersByClubId(Long clubId) throws SQLException {
        String query = "SELECT m.id, m.user_id, u.name, u.email, m.role, m.joined_at " +
                       "FROM member m JOIN \"user\" u ON m.user_id = u.id " +
                       "WHERE m.club_id = ? " +
                       "ORDER BY m.joined_at ASC";
        
        List<ClubMemberResponse> members = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, clubId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    members.add(ClubMemberResponse.builder()
                            .id(rs.getLong("id"))
                            .userId(rs.getLong("user_id"))
                            .name(rs.getString("name"))
                            .email(rs.getString("email"))
                            .role(Role.valueOf(rs.getString("role")))
                            .joinedAt(rs.getTimestamp("joined_at"))
                            .build());
                }
            }
        }
        return members;
    }

    public void updateRole(Long userId, Long clubId, Role role) throws SQLException {
        String query = "UPDATE member SET role = ?::member_role WHERE user_id = ? AND club_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, role.name());
            stmt.setLong(2, userId);
            stmt.setLong(3, clubId);
            stmt.executeUpdate();
        }
    }

    public void deleteByUserAndClub(Long userId, Long clubId) throws SQLException {
        String query = "DELETE FROM member WHERE user_id = ? AND club_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, userId);
            stmt.setLong(2, clubId);
            stmt.executeUpdate();
        }
    }
}
