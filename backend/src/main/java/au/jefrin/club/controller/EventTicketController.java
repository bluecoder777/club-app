package au.jefrin.club.controller;

import au.jefrin.club.dto.EventTicketRequest;
import au.jefrin.club.service.ClubEventService;
import au.jefrin.common.config.ServiceFactory;
import au.jefrin.common.controller.AuthenticatedController;
import au.jefrin.common.dto.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@WebServlet("/api/v1/clubs/events/tickets")
public class EventTicketController extends AuthenticatedController<EventTicketRequest> {
    private ClubEventService eventService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.eventService = ServiceFactory.createClubEventService();
    }

    @Override
    protected Class<EventTicketRequest> getRequestClass() {
        return EventTicketRequest.class;
    }

    @Override
    protected ApiResponse<?> processAuthenticatedPost(EventTicketRequest request,
                                                      HttpServletRequest req,
                                                      Long userId) {
        return ApiResponse.success(eventService.reserveTicket(request.getEventId(), userId));
    }

    @Override
    protected ApiResponse<?> processAuthenticatedDelete(EventTicketRequest request,
                                                        HttpServletRequest req,
                                                        Long userId) {
        return ApiResponse.success(eventService.cancelTicket(request.getEventId(), userId));
    }
}
