package au.jefrin.club.controller;

import au.jefrin.club.dto.ClubMemberResponse;
import au.jefrin.club.service.ClubService;
import au.jefrin.common.controller.AuthenticatedController;
import au.jefrin.common.dto.ApiResponse;
import au.jefrin.common.dto.EmptyRequest;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@WebServlet("/api/v1/clubs/members")
public class ListClubMembersController extends AuthenticatedController<EmptyRequest> {

    private ClubService clubService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.clubService = new ClubService();
    }

    @Override
    protected Class<EmptyRequest> getRequestClass() {
        return EmptyRequest.class;
    }

    @Override
    protected ApiResponse<?> processAuthenticatedRequest(EmptyRequest request, HttpServletRequest req, Long userId) throws Exception {
        String clubIdStr = req.getParameter("clubId");
        if (clubIdStr == null || clubIdStr.trim().isEmpty()) {
            throw new IllegalArgumentException("clubId parameter is required");
        }

        Long clubId;
        try {
            clubId = Long.parseLong(clubIdStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid clubId format");
        }

        List<ClubMemberResponse> members = clubService.getClubMembers(clubId, userId);
        return ApiResponse.success(members);
    }
}

