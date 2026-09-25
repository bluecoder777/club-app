package au.jefrin.club.controller;

import au.jefrin.club.dto.EditClubRequest;
import au.jefrin.common.dto.ApiResponse;
import au.jefrin.club.dto.ClubResponse;
import au.jefrin.club.service.ClubService;
import au.jefrin.common.config.ServiceFactory;
import au.jefrin.common.controller.AuthenticatedController;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@WebServlet("/api/v1/clubs/edit")
public class EditClubController extends AuthenticatedController<EditClubRequest> {

    private ClubService clubService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.clubService = ServiceFactory.createClubService();
    }

    @Override
    protected Class<EditClubRequest> getRequestClass() {
        return EditClubRequest.class;
    }

    @Override
    protected ApiResponse<?> processAuthenticatedRequest(EditClubRequest request, HttpServletRequest req, Long userId) throws Exception {
        ClubResponse club = clubService.editClub(request, userId);
        return ApiResponse.success(club);
    }
}
