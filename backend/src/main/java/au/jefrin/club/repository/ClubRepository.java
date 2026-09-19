package au.jefrin.club.repository;

import au.jefrin.common.config.DatabaseConfig;
import au.jefrin.club.model.Club;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import au.jefrin.club.dto.ClubResponse;
import au.jefrin.user.dto.UserResponse;
import au.jefrin.club.model.Role;

public class ClubRepository {

    public Club save(Club club) throws SQLException {
        String query = "INSERT INTO clubs (name, description, created_by) VALUES (?, ?, ?) RETURNING id, date_of_creation";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, club.getName());
            stmt.setString(2, club.getDescription());
            stmt.setLong(3, club.getCreatedBy());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    club.setId(rs.getLong("id"));
                    club.setDateOfCreation(rs.getTimestamp("date_of_creation"));
                    return club;
                }
            }
        }
        throw new SQLException("Failed to create club");
    }

    public Club findById(Long id) throws SQLException {
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
                    club.setDateOfCreation(rs.getTimestamp("date_of_creation"));
                    club.setCreatedBy(rs.getLong("created_by"));
                    club.setMemberCount(rs.getInt("member_count"));
                    return club;
                }
            }
        }
        return null;
    }


    public void update(Club club) throws SQLException {
        String query = "UPDATE clubs SET name = ?, description = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, club.getName());
            stmt.setString(2, club.getDescription());
            stmt.setLong(3, club.getId());
            stmt.executeUpdate();
        }
    }

    public List<Club> findAll() throws SQLException {
        String query = "SELECT c.id, c.name, c.description, c.date_of_creation, c.created_by, COUNT(m.id) as member_count " +
                       "FROM clubs c LEFT JOIN member m ON c.id = m.club_id " +
                       "GROUP BY c.id ORDER BY c.date_of_creation DESC";
        List<Club> clubs = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Club club = new Club();
                club.setId(rs.getLong("id"));
                club.setName(rs.getString("name"));
                club.setDescription(rs.getString("description"));
                club.setDateOfCreation(rs.getTimestamp("date_of_creation"));
                club.setCreatedBy(rs.getLong("created_by"));
                club.setMemberCount(rs.getInt("member_count"));
                clubs.add(club);
            }
        }
        return clubs;
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
                .dateOfCreation(rs.getTimestamp("date_of_creation"))
                .createdBy(createdBy)
                .memberCount(rs.getInt("member_count"))
                .isMember(rs.getBoolean("is_member"))
                .currentUserRole(role)
                .build();
    }

    public ClubResponse findClubResponseById(Long id, Long currentUserId) throws SQLException {
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
                    return buildResponseFromResultSet(rs);
                }
            }
        }
        return null;
    }

    public List<ClubResponse> findAllClubResponses(Long currentUserId) throws SQLException {
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
    }
}
