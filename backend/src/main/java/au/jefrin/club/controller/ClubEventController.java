package au.jefrin.club.controller;

import au.jefrin.club.dto.ClubEventResponse;
import au.jefrin.club.dto.CreateEventRequest;
import au.jefrin.club.service.ClubEventService;
import au.jefrin.common.config.ServiceFactory;
import au.jefrin.common.controller.AuthenticatedController;
import au.jefrin.common.dto.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@WebServlet("/api/v1/clubs/events")
public class ClubEventController extends AuthenticatedController<CreateEventRequest> {
    private ClubEventService eventService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.eventService = ServiceFactory.createClubEventService();
    }

    @Override
    protected Class<CreateEventRequest> getRequestClass() {
        return CreateEventRequest.class;
    }

    @Override
    protected ApiResponse<?> processAuthenticatedGet(HttpServletRequest req, Long userId) {
        Long clubId = parseRequiredId(req.getParameter("clubId"), "clubId");
        List<ClubEventResponse> events = eventService.listEvents(clubId, userId);
        return ApiResponse.success(events);
    }

    @Override
    protected ApiResponse<?> processAuthenticatedPost(CreateEventRequest request,
                                                       HttpServletRequest req,
                                                       Long userId) {
        return ApiResponse.success(eventService.createEvent(request, userId));
    }

    private Long parseRequiredId(String value, String parameterName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(parameterName + " parameter is required");
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Invalid " + parameterName + " format");
        }
    }
}
