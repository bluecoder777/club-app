package au.jefrin.service;

import au.jefrin.dto.request.CreateClubRequest;
import au.jefrin.model.Club;
import au.jefrin.repository.ClubRepository;

import java.sql.SQLException;
import java.sql.Timestamp;

public class ClubService {

    private final ClubRepository clubRepository;

    public ClubService() {
        this.clubRepository = new ClubRepository();
    }

    public Club createClub(CreateClubRequest request, Long userId) throws SQLException {
        Club club = Club.builder()
                .name(request.getName())
                .description(request.getDescription())
                .dateOfCreation(new Timestamp(System.currentTimeMillis()))
                .createdBy(userId)
                .build();
                
        return clubRepository.save(club);
    }
}

