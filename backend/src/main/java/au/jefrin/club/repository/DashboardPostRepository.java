package au.jefrin.club.repository;

import au.jefrin.club.dto.DashboardPostResponse;
import au.jefrin.club.model.DashboardPost;

import java.util.List;
import java.util.Optional;

public interface DashboardPostRepository {
    DashboardPost save(DashboardPost post);

    Optional<DashboardPost> findById(Long id);

    void update(DashboardPost post);

    Optional<DashboardPostResponse> findPostResponseById(Long id);

    List<DashboardPostResponse> findAllPostResponsesByClubId(Long clubId);
}
