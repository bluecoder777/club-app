package au.jefrin.club.controller;

import au.jefrin.club.dto.CreatePostRequest;
import au.jefrin.club.dto.DashboardPostPayload;
import au.jefrin.club.dto.DashboardPostResponse;
import au.jefrin.club.dto.EditPostRequest;
import au.jefrin.club.service.DashboardService;
import au.jefrin.common.controller.AuthenticatedController;
import au.jefrin.common.dto.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@WebServlet("/api/v1/clubs/dashboard/posts")
public class DashboardPostController extends AuthenticatedController<DashboardPostPayload> {

    private DashboardService dashboardService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.dashboardService = new DashboardService();
    }

    @Override
    protected Class<DashboardPostPayload> getRequestClass() {
        return DashboardPostPayload.class;
    }

    @Override
    protected ApiResponse<?> processAuthenticatedRequest(DashboardPostPayload request, HttpServletRequest req, Long userId) throws Exception {
        String method = req.getMethod();

        if ("POST".equalsIgnoreCase(method)) {
            CreatePostRequest createReq = new CreatePostRequest();
            createReq.setClubId(request.getClubId());
            createReq.setTitle(request.getTitle());
            createReq.setDescription(request.getDescription());
            
            DashboardPostResponse response = dashboardService.createPost(createReq, userId);
            return ApiResponse.success(response);
            
        } else if ("PATCH".equalsIgnoreCase(method)) {
            EditPostRequest editReq = new EditPostRequest();
            editReq.setPostId(request.getPostId());
            editReq.setTitle(request.getTitle());
            editReq.setDescription(request.getDescription());
            
            DashboardPostResponse response = dashboardService.editPost(editReq, userId);
            return ApiResponse.success(response);
            
        } else if ("GET".equalsIgnoreCase(method)) {
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

            List<DashboardPostResponse> posts = dashboardService.listPosts(clubId, userId);
            return ApiResponse.success(posts);
            
        } else {
            throw new IllegalArgumentException("Unsupported HTTP method for this endpoint");
        }
    }
}

