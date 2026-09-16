package au.jefrin.club.controller;
import au.jefrin.common.controller.AuthenticatedController;

import au.jefrin.club.dto.CreateClubRequest;
import au.jefrin.common.dto.ApiResponse;
import au.jefrin.club.model.Club;
import au.jefrin.club.service.ClubService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@WebServlet("/api/v1/clubs")
public class CreateClubController extends AuthenticatedController<CreateClubRequest> {

    private ClubService clubService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.clubService = new ClubService();
    }

    @Override
    protected Class<CreateClubRequest> getRequestClass() {
        return CreateClubRequest.class;
    }

    @Override
    protected ApiResponse<?> processAuthenticatedRequest(CreateClubRequest request, HttpServletRequest req, Long userId) throws Exception {
        Club club = clubService.createClub(request, userId);
        return ApiResponse.success(club);
    }
}
