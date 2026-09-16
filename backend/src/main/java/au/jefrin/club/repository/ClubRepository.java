package au.jefrin.club.repository;

import au.jefrin.common.config.DatabaseConfig;
import au.jefrin.club.model.Club;

import java.sql.*;

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
        String query = "SELECT id, name, description, date_of_creation, created_by FROM clubs WHERE id = ?";
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
}
