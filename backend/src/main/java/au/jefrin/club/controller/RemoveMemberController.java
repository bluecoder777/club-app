package au.jefrin.club.controller;

import au.jefrin.club.dto.RemoveMemberRequest;
import au.jefrin.common.dto.ApiResponse;
import au.jefrin.club.service.ClubService;
import au.jefrin.common.config.ServiceFactory;
import au.jefrin.common.controller.AuthenticatedController;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@WebServlet("/api/v1/clubs/members/remove/*")
public class RemoveMemberController extends AuthenticatedController<RemoveMemberRequest> {

    private ClubService clubService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.clubService = ServiceFactory.createClubService();
    }

    @Override
    protected Class<RemoveMemberRequest> getRequestClass() {
        return RemoveMemberRequest.class;
    }

    @Override
    protected ApiResponse<?> processAuthenticatedRequest(RemoveMemberRequest request, HttpServletRequest req, Long userId) throws Exception {
        String pathInfo = req.getPathInfo();
        if (pathInfo != null && pathInfo.length() > 1) {
            String[] parts = pathInfo.substring(1).split("/");
            if (parts.length >= 2) {
                request.setClubId(Long.valueOf(parts[0]));
                request.setTargetUserId(Long.valueOf(parts[1]));
            }
        }
        
        clubService.removeMember(request, userId);
        return ApiResponse.success("Member removed successfully");
    }
}
