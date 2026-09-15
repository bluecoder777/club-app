package au.jefrin.controller;

import au.jefrin.dto.request.CreateClubRequest;
import au.jefrin.dto.response.ApiResponse;
import au.jefrin.dto.response.ClubResponse;
import au.jefrin.model.Club;
import au.jefrin.service.ClubService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/v1/clubs")
public class ClubController extends BaseController<CreateClubRequest> {

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
    protected int getSuccessStatusCode() {
        return HttpServletResponse.SC_CREATED; // 201 Created
    }

    @Override
    protected ApiResponse<?> processRequest(CreateClubRequest request, HttpServletRequest httpRequest) throws Exception {
        if (!request.isValid()) {
            throw new IllegalArgumentException("Missing required fields (name)");
        }

        Long userId = (Long) httpRequest.getAttribute("userId");
        if (userId == null) {
            throw new au.jefrin.exception.UnauthorizedException("User is not authenticated");
        }

        Club club = clubService.createClub(request, userId);
        return ApiResponse.success(ClubResponse.fromClub(club));
    }
}
