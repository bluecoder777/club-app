package au.jefrin.club.repository;

import au.jefrin.club.dto.ClubEventResponse;
import au.jefrin.club.model.ClubEvent;
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

public class JdbcClubEventRepository implements ClubEventRepository {

    @Override
    public ClubEvent save(ClubEvent event) {
        return JdbcOperation.execute("Failed to create club event", () -> {
            String query = "INSERT INTO club_event " +
                    "(club_id, name, description, venue, event_time, capacity, created_by) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id, created_at";

            try (Connection connection = DatabaseConfig.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setLong(1, event.getClubId());
                statement.setString(2, event.getName());
                statement.setString(3, event.getDescription());
                statement.setString(4, event.getVenue());
                statement.setObject(5, event.getEventTime());
                statement.setInt(6, event.getCapacity());
                statement.setLong(7, event.getCreatedBy());

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        event.setId(resultSet.getLong("id"));
                        event.setCreatedAt(resultSet.getObject("created_at", LocalDateTime.class));
                        return event;
                    }
                }
            }
            throw new SQLException("Club event insert returned no row");
        });
    }

    @Override
    public Optional<ClubEvent> findById(Long eventId) {
        return JdbcOperation.execute("Failed to find club event", () -> {
            String query = "SELECT id, club_id, name, description, venue, event_time, capacity, created_by, created_at " +
                    "FROM club_event WHERE id = ?";

            try (Connection connection = DatabaseConfig.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setLong(1, eventId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        ClubEvent event = new ClubEvent();
                        event.setId(resultSet.getLong("id"));
                        event.setClubId(resultSet.getLong("club_id"));
                        event.setName(resultSet.getString("name"));
                        event.setDescription(resultSet.getString("description"));
                        event.setVenue(resultSet.getString("venue"));
                        event.setEventTime(resultSet.getObject("event_time", LocalDateTime.class));
                        event.setCapacity(resultSet.getInt("capacity"));
                        event.setCreatedBy(resultSet.getLong("created_by"));
                        event.setCreatedAt(resultSet.getObject("created_at", LocalDateTime.class));
                        return Optional.of(event);
                    }
                }
            }
            return Optional.empty();
        });
    }

    @Override
    public Optional<ClubEventResponse> findResponseById(Long eventId, Long currentUserId) {
        return JdbcOperation.execute("Failed to load club event", () -> {
            String query = responseQuery() + " WHERE e.id = ? GROUP BY e.id";
            try (Connection connection = DatabaseConfig.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setLong(1, currentUserId);
                statement.setLong(2, eventId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return Optional.of(buildResponse(resultSet));
                    }
                }
            }
            return Optional.empty();
        });
    }

    @Override
    public List<ClubEventResponse> findAllResponsesByClubId(Long clubId, Long currentUserId) {
        return JdbcOperation.execute("Failed to list club events", () -> {
            String query = responseQuery() +
                    " WHERE e.club_id = ? GROUP BY e.id ORDER BY e.event_time ASC";
            List<ClubEventResponse> events = new ArrayList<>();

            try (Connection connection = DatabaseConfig.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setLong(1, currentUserId);
                statement.setLong(2, clubId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        events.add(buildResponse(resultSet));
                    }
                }
            }
            return events;
        });
    }

    // adds ticket counts and the current users booking state
    private String responseQuery() {
        return "SELECT e.id, e.club_id, e.name, e.description, e.venue, e.event_time, e.capacity, " +
                "COUNT(t.id) AS tickets_issued, " +
                "EXISTS (SELECT 1 FROM event_ticket own_ticket " +
                "JOIN member own_member ON own_ticket.member_id = own_member.id " +
                "WHERE own_ticket.event_id = e.id AND own_member.user_id = ?) AS has_ticket " +
                "FROM club_event e LEFT JOIN event_ticket t ON t.event_id = e.id";
    }

    private ClubEventResponse buildResponse(ResultSet resultSet) throws SQLException {
        return ClubEventResponse.builder()
                .id(resultSet.getLong("id"))
                .clubId(resultSet.getLong("club_id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .venue(resultSet.getString("venue"))
                .eventTime(resultSet.getObject("event_time", LocalDateTime.class))
                .capacity(resultSet.getInt("capacity"))
                .ticketsIssued(resultSet.getInt("tickets_issued"))
                .hasTicket(resultSet.getBoolean("has_ticket"))
                .build();
    }
}
