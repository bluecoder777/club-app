package au.jefrin.club.controller;
import au.jefrin.common.controller.AuthenticatedController;

import au.jefrin.club.dto.JoinClubRequest;
import au.jefrin.common.dto.ApiResponse;
import au.jefrin.club.service.ClubService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@WebServlet("/api/v1/clubs/join")
public class JoinClubController extends AuthenticatedController<JoinClubRequest> {

    private ClubService clubService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.clubService = new ClubService();
    }

    @Override
    protected Class<JoinClubRequest> getRequestClass() {
        return JoinClubRequest.class;
    }

    @Override
    protected ApiResponse<?> processAuthenticatedRequest(JoinClubRequest request, HttpServletRequest req, Long userId) throws Exception {
        clubService.joinClub(request.getClubId(), userId);
        return ApiResponse.success("Successfully joined the club");
    }
}
