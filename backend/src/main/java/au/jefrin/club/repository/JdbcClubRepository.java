package au.jefrin.club.repository;

import au.jefrin.club.dto.ClubResponse;
import au.jefrin.club.model.Club;
import au.jefrin.club.model.Member;
import au.jefrin.club.model.Role;
import au.jefrin.common.config.DatabaseConfig;
import au.jefrin.common.repository.JdbcOperation;
import au.jefrin.user.dto.UserResponse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcClubRepository implements ClubRepository {

    @Override
    public Club createWithFoundingMember(Club club, Member foundingMember) {
        return JdbcOperation.execute("Failed to create club with founding member", () -> {
            try (Connection connection = DatabaseConfig.getConnection()) {
                connection.setAutoCommit(false);

                try {
                    insertClub(connection, club);
                    foundingMember.setClubId(club.getId());
                    JdbcMemberRepository.insert(connection, foundingMember);
                    connection.commit();
                    return club;
                } catch (SQLException | RuntimeException exception) {
                    rollback(connection, exception);
                    throw exception;
                }
            }
        });
    }

    private void insertClub(Connection connection, Club club) throws SQLException {
        String query = "INSERT INTO clubs (name, description, created_by) VALUES (?, ?, ?) RETURNING id, date_of_creation";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, club.getName());
            stmt.setString(2, club.getDescription());
            stmt.setLong(3, club.getCreatedBy());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    club.setId(rs.getLong("id"));
                    club.setDateOfCreation(rs.getObject("date_of_creation", LocalDateTime.class));
                    return;
                }
            }
        }
        throw new SQLException("Failed to create club");
    }

    private void rollback(Connection connection, Exception originalException) {
        try {
            connection.rollback();
        } catch (SQLException rollbackException) {
            originalException.addSuppressed(rollbackException);
        }
    }

    @Override
    public Optional<Club> findById(Long id) {
        return JdbcOperation.execute("Failed to find club by ID", () -> {
            String query = "SELECT c.id, c.name, c.description, c.date_of_creation, c.created_by, COUNT(m.id) as member_count " +
                    "FROM clubs c LEFT JOIN member m ON c.id = m.club_id " +
                    "WHERE c.id = ? GROUP BY c.id";
            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setLong(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Club club = new Club();
                        club.setId(rs.getLong("id"));
                        club.setName(rs.getString("name"));
                        club.setDescription(rs.getString("description"));
                        club.setDateOfCreation(rs.getObject("date_of_creation", LocalDateTime.class));
                        club.setCreatedBy(rs.getLong("created_by"));
                        club.setMemberCount(rs.getInt("member_count"));
                        return Optional.of(club);
                    }
                }
            }
            return Optional.empty();
        });
    }


    @Override
    public void update(Club club) {
        JdbcOperation.execute("Failed to update club", () -> {
            String query = "UPDATE clubs SET name = ?, description = ? WHERE id = ?";
            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, club.getName());
                stmt.setString(2, club.getDescription());
                stmt.setLong(3, club.getId());
                stmt.executeUpdate();
            }
        });
    }

    private ClubResponse buildResponseFromResultSet(ResultSet rs) throws SQLException {
        UserResponse createdBy = UserResponse.builder()
                .id(rs.getLong("created_by_id"))
                .name(rs.getString("created_by_name"))
                .email(rs.getString("created_by_email"))
                .build();

        String roleStr = rs.getString("current_user_role");
        Role role = roleStr != null ? Role.valueOf(roleStr) : null;

        return ClubResponse.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .dateOfCreation(rs.getObject("date_of_creation", LocalDateTime.class))
                .createdBy(createdBy)
                .memberCount(rs.getInt("member_count"))
                .isMember(rs.getBoolean("is_member"))
                .currentUserRole(role)
                .build();
    }

    @Override
    public Optional<ClubResponse> findClubResponseById(Long id, Long currentUserId) {
        return JdbcOperation.execute("Failed to load club response", () -> {
            String query = "SELECT c.id, c.name, c.description, c.date_of_creation, " +
                    "cb.id as created_by_id, cb.name as created_by_name, cb.email as created_by_email, " +
                    "COUNT(m.id) as member_count, " +
                    "COUNT(CASE WHEN m.user_id = ? THEN 1 END) > 0 as is_member, " +
                    "MAX(CASE WHEN m.user_id = ? THEN m.role::text END) as current_user_role " +
                    "FROM clubs c " +
                    "JOIN \"user\" cb ON c.created_by = cb.id " +
                    "LEFT JOIN member m ON c.id = m.club_id " +
                    "WHERE c.id = ? GROUP BY c.id, cb.id";
            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setLong(1, currentUserId);
                stmt.setLong(2, currentUserId);
                stmt.setLong(3, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(buildResponseFromResultSet(rs));
                    }
                }
            }
            return Optional.empty();
        });
    }

    @Override
    public List<ClubResponse> findAllClubResponses(Long currentUserId) {
        return JdbcOperation.execute("Failed to list clubs", () -> {
            String query = "SELECT c.id, c.name, c.description, c.date_of_creation, " +
                    "cb.id as created_by_id, cb.name as created_by_name, cb.email as created_by_email, " +
                    "COUNT(m.id) as member_count, " +
                    "COUNT(CASE WHEN m.user_id = ? THEN 1 END) > 0 as is_member, " +
                    "MAX(CASE WHEN m.user_id = ? THEN m.role::text END) as current_user_role " +
                    "FROM clubs c " +
                    "JOIN \"user\" cb ON c.created_by = cb.id " +
                    "LEFT JOIN member m ON c.id = m.club_id " +
                    "GROUP BY c.id, cb.id ORDER BY c.date_of_creation DESC";
            List<ClubResponse> responses = new ArrayList<>();
            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setLong(1, currentUserId);
                stmt.setLong(2, currentUserId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        responses.add(buildResponseFromResultSet(rs));
                    }
                }
            }
            return responses;
        });
    }
}
