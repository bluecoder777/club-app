package au.jefrin.club.repository;

import au.jefrin.club.dto.ClubResponse;
import au.jefrin.club.model.Club;

import java.sql.SQLException;
import java.util.List;

public interface ClubRepository {
    Club save(Club club) throws SQLException;

    Club findById(Long id) throws SQLException;

    void update(Club club) throws SQLException;

    ClubResponse findClubResponseById(Long id, Long currentUserId) throws SQLException;

    List<ClubResponse> findAllClubResponses(Long currentUserId) throws SQLException;
}
