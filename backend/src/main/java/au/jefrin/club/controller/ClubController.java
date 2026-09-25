package au.jefrin.club.controller;

import au.jefrin.common.controller.AuthenticatedController;
import au.jefrin.club.dto.CreateClubRequest;
import au.jefrin.common.dto.ApiResponse;
import au.jefrin.club.dto.ClubResponse;
import au.jefrin.club.service.ClubService;
import au.jefrin.common.config.ServiceFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@WebServlet(urlPatterns = {"/api/v1/clubs", "/api/v1/clubs/*"})
public class ClubController extends AuthenticatedController<CreateClubRequest> {

    private ClubService clubService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.clubService = ServiceFactory.createClubService();
    }

    @Override
    protected Class<CreateClubRequest> getRequestClass() {
        return CreateClubRequest.class;
    }

    @Override
    protected ApiResponse<?> processAuthenticatedRequest(CreateClubRequest request, HttpServletRequest req, Long userId) throws Exception {
        if ("GET".equalsIgnoreCase(req.getMethod())) {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                List<ClubResponse> clubs = clubService.getAllClubs(userId);
                return ApiResponse.success(clubs);
            } else {
                String idPart = pathInfo.substring(1);
                try {
                    Long clubId = Long.parseLong(idPart);
                    ClubResponse club = clubService.getClub(clubId, userId);
                    return ApiResponse.success(club);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid club ID format");
                }
            }
        } else if ("POST".equalsIgnoreCase(req.getMethod())) {
            ClubResponse club = clubService.createClub(request, userId);
            return ApiResponse.success(club);
        } else {
            throw new IllegalArgumentException("Unsupported HTTP method");
        }
    }
}
