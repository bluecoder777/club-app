package au.jefrin.club.repository;

import au.jefrin.common.config.DatabaseConfig;
import au.jefrin.club.model.DashboardPost;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import au.jefrin.club.dto.DashboardPostResponse;
import au.jefrin.user.dto.UserResponse;

public class DashboardPostRepository {

    public DashboardPost save(DashboardPost post) throws SQLException {
        String query = "INSERT INTO dashboard_post (club_id, title, description, created_by, last_updated_by) " +
                       "VALUES (?, ?, ?, ?, ?) RETURNING id, created_at, updated_at";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setLong(1, post.getClubId());
            stmt.setString(2, post.getTitle());
            stmt.setString(3, post.getDescription());
            stmt.setLong(4, post.getCreatedBy());
            if (post.getLastUpdatedBy() != null) {
                stmt.setLong(5, post.getLastUpdatedBy());
            } else {
                stmt.setNull(5, Types.BIGINT);
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    post.setId(rs.getLong("id"));
                    post.setCreatedAt(rs.getTimestamp("created_at"));
                    post.setUpdatedAt(rs.getTimestamp("updated_at"));
                    return post;
                }
            }
        }
        throw new SQLException("Failed to save dashboard post");
    }

    public DashboardPost findById(Long id) throws SQLException {
        String query = "SELECT id, club_id, title, description, created_by, last_updated_by, created_at, updated_at " +
                       "FROM dashboard_post WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    DashboardPost post = new DashboardPost();
                    post.setId(rs.getLong("id"));
                    post.setClubId(rs.getLong("club_id"));
                    post.setTitle(rs.getString("title"));
                    post.setDescription(rs.getString("description"));
                    post.setCreatedBy(rs.getLong("created_by"));
                    long lastUpdatedBy = rs.getLong("last_updated_by");
                    if (!rs.wasNull()) {
                        post.setLastUpdatedBy(lastUpdatedBy);
                    }
                    post.setCreatedAt(rs.getTimestamp("created_at"));
                    post.setUpdatedAt(rs.getTimestamp("updated_at"));
                    return post;
                }
            }
        }
        return null;
    }

    public void update(DashboardPost post) throws SQLException {
        String query = "UPDATE dashboard_post SET title = ?, description = ?, last_updated_by = ?, updated_at = CURRENT_TIMESTAMP " +
                       "WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, post.getTitle());
            stmt.setString(2, post.getDescription());
            stmt.setLong(3, post.getLastUpdatedBy());
            stmt.setLong(4, post.getId());
            stmt.executeUpdate();
        }
    }

    public List<DashboardPost> findAllByClubId(Long clubId) throws SQLException {
        String query = "SELECT id, club_id, title, description, created_by, last_updated_by, created_at, updated_at " +
                       "FROM dashboard_post WHERE club_id = ? ORDER BY created_at DESC";
        List<DashboardPost> posts = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, clubId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DashboardPost post = new DashboardPost();
                    post.setId(rs.getLong("id"));
                    post.setClubId(rs.getLong("club_id"));
                    post.setTitle(rs.getString("title"));
                    post.setDescription(rs.getString("description"));
                    post.setCreatedBy(rs.getLong("created_by"));
                    long lastUpdatedBy = rs.getLong("last_updated_by");
                    if (!rs.wasNull()) {
                        post.setLastUpdatedBy(lastUpdatedBy);
                    }
                    post.setCreatedAt(rs.getTimestamp("created_at"));
                    post.setUpdatedAt(rs.getTimestamp("updated_at"));
                    posts.add(post);
                }
            }
        }
        return posts;
    }

    private DashboardPostResponse buildResponseFromResultSet(ResultSet rs) throws SQLException {
        UserResponse createdBy = UserResponse.builder()
                .id(rs.getLong("created_by_id"))
                .name(rs.getString("created_by_name"))
                .email(rs.getString("created_by_email"))
                .build();
                
        UserResponse updatedBy = null;
        if (rs.getObject("updated_by_id") != null) {
            updatedBy = UserResponse.builder()
                    .id(rs.getLong("updated_by_id"))
                    .name(rs.getString("updated_by_name"))
                    .email(rs.getString("updated_by_email"))
                    .build();
        }

        return DashboardPostResponse.builder()
                .id(rs.getLong("id"))
                .clubId(rs.getLong("club_id"))
                .title(rs.getString("title"))
                .description(rs.getString("description"))
                .createdBy(createdBy)
                .lastUpdatedBy(updatedBy)
                .createdAt(rs.getTimestamp("created_at"))
                .updatedAt(rs.getTimestamp("updated_at"))
                .build();
    }

    public DashboardPostResponse findPostResponseById(Long id) throws SQLException {
        String query = "SELECT dp.id, dp.club_id, dp.title, dp.description, dp.created_at, dp.updated_at, " +
                       "cb.id as created_by_id, cb.name as created_by_name, cb.email as created_by_email, " +
                       "ub.id as updated_by_id, ub.name as updated_by_name, ub.email as updated_by_email " +
                       "FROM dashboard_post dp " +
                       "JOIN \"user\" cb ON dp.created_by = cb.id " +
                       "LEFT JOIN \"user\" ub ON dp.last_updated_by = ub.id " +
                       "WHERE dp.id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return buildResponseFromResultSet(rs);
                }
            }
        }
        return null;
    }

    public List<DashboardPostResponse> findAllPostResponsesByClubId(Long clubId) throws SQLException {
        String query = "SELECT dp.id, dp.club_id, dp.title, dp.description, dp.created_at, dp.updated_at, " +
                       "cb.id as created_by_id, cb.name as created_by_name, cb.email as created_by_email, " +
                       "ub.id as updated_by_id, ub.name as updated_by_name, ub.email as updated_by_email " +
                       "FROM dashboard_post dp " +
                       "JOIN \"user\" cb ON dp.created_by = cb.id " +
                       "LEFT JOIN \"user\" ub ON dp.last_updated_by = ub.id " +
                       "WHERE dp.club_id = ? ORDER BY dp.created_at DESC";
        List<DashboardPostResponse> posts = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, clubId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    posts.add(buildResponseFromResultSet(rs));
                }
            }
        }
        return posts;
    }
}

