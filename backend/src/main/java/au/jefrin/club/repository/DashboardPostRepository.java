package au.jefrin.club.repository;

import au.jefrin.club.dto.DashboardPostResponse;
import au.jefrin.club.model.DashboardPost;

import java.sql.SQLException;
import java.util.List;

public interface DashboardPostRepository {
    DashboardPost save(DashboardPost post) throws SQLException;

    DashboardPost findById(Long id) throws SQLException;

    void update(DashboardPost post) throws SQLException;

    DashboardPostResponse findPostResponseById(Long id) throws SQLException;

    List<DashboardPostResponse> findAllPostResponsesByClubId(Long clubId) throws SQLException;
}
