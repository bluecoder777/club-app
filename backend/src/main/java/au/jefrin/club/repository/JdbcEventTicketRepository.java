package au.jefrin.club.repository;

import au.jefrin.club.model.TicketReservationResult;
import au.jefrin.common.config.DatabaseConfig;
import au.jefrin.common.repository.JdbcOperation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcEventTicketRepository implements EventTicketRepository {

    // locks the event while checking membership and capacity
    @Override
    public TicketReservationResult reserve(Long eventId, Long userId) {
        return JdbcOperation.execute("Failed to reserve event ticket", () -> {
            try (Connection connection = DatabaseConfig.getConnection()) {
                connection.setAutoCommit(false);
                try {
                    EventCapacity event = findAndLockEvent(connection, eventId);
                    if (event == null) {
                        return rollbackWith(connection, TicketReservationResult.EVENT_NOT_FOUND);
                    }

                    Long memberId = findMemberId(connection, event.clubId(), userId);
                    if (memberId == null) {
                        return rollbackWith(connection, TicketReservationResult.MEMBERSHIP_REQUIRED);
                    }

                    if (ticketExists(connection, eventId, memberId)) {
                        return rollbackWith(connection, TicketReservationResult.ALREADY_RESERVED);
                    }

                    if (ticketCount(connection, eventId) >= event.capacity()) {
                        return rollbackWith(connection, TicketReservationResult.SOLD_OUT);
                    }

                    insertTicket(connection, eventId, memberId);
                    connection.commit();
                    return TicketReservationResult.RESERVED;
                } catch (SQLException | RuntimeException exception) {
                    rollback(connection, exception);
                    throw exception;
                }
            }
        });
    }

    @Override
    public boolean cancel(Long eventId, Long userId) {
        return JdbcOperation.execute("Failed to cancel event ticket", () -> {
            String query = "DELETE FROM event_ticket t USING member m " +
                    "WHERE t.member_id = m.id AND t.event_id = ? AND m.user_id = ?";
            try (Connection connection = DatabaseConfig.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setLong(1, eventId);
                statement.setLong(2, userId);
                return statement.executeUpdate() > 0;
            }
        });
    }

    private EventCapacity findAndLockEvent(Connection connection, Long eventId) throws SQLException {
        String query = "SELECT club_id, capacity FROM club_event WHERE id = ? FOR UPDATE";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, eventId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new EventCapacity(resultSet.getLong("club_id"), resultSet.getInt("capacity"));
                }
            }
        }
        return null;
    }

    private Long findMemberId(Connection connection, Long clubId, Long userId) throws SQLException {
        String query = "SELECT id FROM member WHERE club_id = ? AND user_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, clubId);
            statement.setLong(2, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getLong("id") : null;
            }
        }
    }

    private boolean ticketExists(Connection connection, Long eventId, Long memberId) throws SQLException {
        String query = "SELECT 1 FROM event_ticket WHERE event_id = ? AND member_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, eventId);
            statement.setLong(2, memberId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private int ticketCount(Connection connection, Long eventId) throws SQLException {
        String query = "SELECT COUNT(*) FROM event_ticket WHERE event_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, eventId);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1);
            }
        }
    }

    private void insertTicket(Connection connection, Long eventId, Long memberId) throws SQLException {
        String query = "INSERT INTO event_ticket (event_id, member_id) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, eventId);
            statement.setLong(2, memberId);
            statement.executeUpdate();
        }
    }

    private TicketReservationResult rollbackWith(Connection connection, TicketReservationResult result)
            throws SQLException {
        connection.rollback();
        return result;
    }

    private void rollback(Connection connection, Exception originalException) {
        try {
            connection.rollback();
        } catch (SQLException rollbackException) {
            originalException.addSuppressed(rollbackException);
        }
    }

    private record EventCapacity(Long clubId, int capacity) {
    }
}
