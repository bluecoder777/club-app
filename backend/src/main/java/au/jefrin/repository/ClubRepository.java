package au.jefrin.repository;

import au.jefrin.config.DatabaseConfig;
import au.jefrin.model.Club;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ClubRepository {

    public Club save(Club club) throws SQLException {
        String sql = "INSERT INTO clubs (name, description, date_of_creation, created_by) VALUES (?, ?, ?, ?) RETURNING id";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, club.getName());
            statement.setString(2, club.getDescription());
            statement.setTimestamp(3, club.getDateOfCreation());
            statement.setLong(4, club.getCreatedBy());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating club failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    club.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating club failed, no ID obtained.");
                }
            }
        }
        return club;
    }
}

