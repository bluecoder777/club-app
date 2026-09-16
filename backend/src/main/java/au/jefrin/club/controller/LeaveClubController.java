package au.jefrin.club.controller;

import au.jefrin.club.dto.LeaveClubRequest;
import au.jefrin.common.dto.ApiResponse;
import au.jefrin.club.service.ClubService;
import au.jefrin.common.controller.AuthenticatedController;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@WebServlet("/api/v1/clubs/leave/*")
public class LeaveClubController extends AuthenticatedController<LeaveClubRequest> {

    private ClubService clubService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.clubService = new ClubService();
    }

    @Override
    protected Class<LeaveClubRequest> getRequestClass() {
        return LeaveClubRequest.class;
    }

    @Override
    protected ApiResponse<?> processAuthenticatedRequest(LeaveClubRequest request, HttpServletRequest req, Long userId) throws Exception {
        String pathInfo = req.getPathInfo();
        if (pathInfo != null && pathInfo.length() > 1) {
            String[] parts = pathInfo.substring(1).split("/");
            if (parts.length >= 1) {
                request.setClubId(Long.valueOf(parts[0]));
            }
        }
        
        clubService.leaveClub(request, userId);
        return ApiResponse.success("Successfully left the club");
    }
}
