package au.jefrin.club.repository;

import au.jefrin.club.dto.ClubMemberResponse;
import au.jefrin.club.model.Member;
import au.jefrin.club.model.Role;
import au.jefrin.common.config.DatabaseConfig;
import au.jefrin.common.repository.JdbcOperation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcMemberRepository implements MemberRepository {

    static Member insert(Connection connection, Member member) throws SQLException {
        String query = "INSERT INTO member (user_id, club_id, role) VALUES (?, ?, ?::member_role) RETURNING id, joined_at";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, member.getUserId());
            stmt.setLong(2, member.getClubId());
            stmt.setString(3, member.getRole().name());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    member.setId(rs.getLong("id"));
                    member.setJoinedAt(rs.getObject("joined_at", LocalDateTime.class));
                    return member;
                }
            }
        }
        throw new SQLException("Failed to add member");
    }

    @Override
    public Member save(Member member) {
        return JdbcOperation.execute("Failed to save club member", () -> {
            try (Connection connection = DatabaseConfig.getConnection()) {
                return insert(connection, member);
            }
        });
    }

    @Override
    public boolean existsByUserAndClub(Long userId, Long clubId) {
        return JdbcOperation.execute("Failed to check club membership", () -> {
            String query = "SELECT 1 FROM member WHERE user_id = ? AND club_id = ?";
            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setLong(1, userId);
                stmt.setLong(2, clubId);
                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next();
                }
            }
        });
    }

    @Override
    public boolean hasMembers(Long clubId) {
        return JdbcOperation.execute("Failed to check whether the club has members", () -> {
            String query = "SELECT EXISTS (SELECT 1 FROM member WHERE club_id = ?)";
            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setLong(1, clubId);
                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next() && rs.getBoolean(1);
                }
            }
        });
    }


    @Override
    public Optional<Member> findByUserAndClub(Long userId, Long clubId) {
        return JdbcOperation.execute("Failed to find club membership", () -> {
            String query = "SELECT id, user_id, club_id, role, joined_at FROM member WHERE user_id = ? AND club_id = ?";
            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setLong(1, userId);
                stmt.setLong(2, clubId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Member member = new Member();
                        member.setId(rs.getLong("id"));
                        member.setUserId(rs.getLong("user_id"));
                        member.setClubId(rs.getLong("club_id"));
                        member.setRole(Role.valueOf(rs.getString("role")));
                        member.setJoinedAt(rs.getObject("joined_at", LocalDateTime.class));
                        return Optional.of(member);
                    }
                }
            }
            return Optional.empty();
        });
    }

    @Override
    public List<ClubMemberResponse> findAllMembersByClubId(Long clubId) {
        return JdbcOperation.execute("Failed to list club members", () -> {
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
                                .joinedAt(rs.getObject("joined_at", LocalDateTime.class))
                                .build());
                    }
                }
            }
            return members;
        });
    }

    @Override
    public void updateRole(Long userId, Long clubId, Role role) {
        JdbcOperation.execute("Failed to update club member role", () -> {
            String query = "UPDATE member SET role = ?::member_role WHERE user_id = ? AND club_id = ?";
            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, role.name());
                stmt.setLong(2, userId);
                stmt.setLong(3, clubId);
                stmt.executeUpdate();
            }
        });
    }

    @Override
    public void handOverAdministrationAndRemove(Long departingUserId, Long successorUserId, Long clubId) {
        JdbcOperation.execute("Failed to hand over club administration", () -> {
            try (Connection connection = DatabaseConfig.getConnection()) {
                connection.setAutoCommit(false);

                try {
                    String promoteQuery = "UPDATE member SET role = 'ADMIN'::member_role WHERE user_id = ? AND club_id = ?";
                    try (PreparedStatement promote = connection.prepareStatement(promoteQuery)) {
                        promote.setLong(1, successorUserId);
                        promote.setLong(2, clubId);
                        if (promote.executeUpdate() != 1) {
                            throw new SQLException("The selected successor is no longer a club member");
                        }
                    }

                    String leaveQuery = "DELETE FROM member WHERE user_id = ? AND club_id = ?";
                    try (PreparedStatement leave = connection.prepareStatement(leaveQuery)) {
                        leave.setLong(1, departingUserId);
                        leave.setLong(2, clubId);
                        if (leave.executeUpdate() != 1) {
                            throw new SQLException("The departing admin is no longer a club member");
                        }
                    }

                    connection.commit();
                } catch (SQLException exception) {
                    connection.rollback();
                    throw exception;
                } finally {
                    connection.setAutoCommit(true);
                }
            }
        });
    }

    @Override
    public void deleteByUserAndClub(Long userId, Long clubId) {
        JdbcOperation.execute("Failed to delete club membership", () -> {
            String query = "DELETE FROM member WHERE user_id = ? AND club_id = ?";
            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setLong(1, userId);
                stmt.setLong(2, clubId);
                stmt.executeUpdate();
            }
        });
    }
}
