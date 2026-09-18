package au.jefrin.club.controller;

import au.jefrin.club.dto.UpdateMemberRoleRequest;
import au.jefrin.common.dto.ApiResponse;
import au.jefrin.club.service.ClubService;
import au.jefrin.common.controller.AuthenticatedController;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@WebServlet("/api/v1/clubs/members/role")
public class UpdateMemberRoleController extends AuthenticatedController<UpdateMemberRoleRequest> {

    private ClubService clubService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.clubService = new ClubService();
    }

    @Override
    protected Class<UpdateMemberRoleRequest> getRequestClass() {
        return UpdateMemberRoleRequest.class;
    }

    @Override
    protected ApiResponse<?> processAuthenticatedRequest(UpdateMemberRoleRequest request, HttpServletRequest req, Long userId) throws Exception {
        clubService.updateMemberRole(request, userId);
        return ApiResponse.success("Member role updated successfully");
    }
}
