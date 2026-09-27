package au.jefrin.club.service;

import au.jefrin.club.dto.ClubEventResponse;
import au.jefrin.club.dto.CreateEventRequest;
import au.jefrin.club.model.ClubEvent;
import au.jefrin.club.model.Member;
import au.jefrin.club.model.TicketReservationResult;
import au.jefrin.club.policy.ClubMembershipPolicy;
import au.jefrin.club.repository.ClubEventRepository;
import au.jefrin.club.repository.ClubRepository;
import au.jefrin.club.repository.EventTicketRepository;
import au.jefrin.club.repository.MemberRepository;
import au.jefrin.common.exception.ConflictException;
import au.jefrin.common.exception.DataAccessException;
import au.jefrin.common.exception.UnauthorizedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ClubEventService {
    private final ClubEventRepository eventRepository;
    private final EventTicketRepository ticketRepository;
    private final ClubRepository clubRepository;
    private final MemberRepository memberRepository;
    private final ClubMembershipPolicy membershipPolicy;

    public ClubEventService(ClubEventRepository eventRepository,
                            EventTicketRepository ticketRepository,
                            ClubRepository clubRepository,
                            MemberRepository memberRepository,
                            ClubMembershipPolicy membershipPolicy) {
        this.eventRepository = Objects.requireNonNull(eventRepository, "eventRepository must not be null");
        this.ticketRepository = Objects.requireNonNull(ticketRepository, "ticketRepository must not be null");
        this.clubRepository = Objects.requireNonNull(clubRepository, "clubRepository must not be null");
        this.memberRepository = Objects.requireNonNull(memberRepository, "memberRepository must not be null");
        this.membershipPolicy = Objects.requireNonNull(membershipPolicy, "membershipPolicy must not be null");
    }

    public ClubEventResponse createEvent(CreateEventRequest request, Long userId) {
        validateCreateRequest(request);
        if (clubRepository.findById(request.getClubId()).isEmpty()) {
            throw new IllegalArgumentException("Club not found");
        }

        membershipPolicy.requireAdmin(findMembership(userId, request.getClubId()));

        ClubEvent event = new ClubEvent();
        event.setClubId(request.getClubId());
        event.setName(request.getName().trim());
        event.setDescription(request.getDescription().trim());
        event.setVenue(request.getVenue().trim());
        event.setEventTime(request.getEventTime());
        event.setCapacity(request.getCapacity());
        event.setCreatedBy(userId);

        ClubEvent savedEvent = eventRepository.save(event);
        return eventRepository.findResponseById(savedEvent.getId(), userId)
                .orElseThrow(() -> new DataAccessException("Created club event could not be loaded"));
    }

    public List<ClubEventResponse> listEvents(Long clubId, Long userId) {
        if (clubId == null) {
            throw new IllegalArgumentException("Club ID is required");
        }
        if (clubRepository.findById(clubId).isEmpty()) {
            throw new IllegalArgumentException("Club not found");
        }

        membershipPolicy.requireMember(findMembership(userId, clubId));
        return eventRepository.findAllResponsesByClubId(clubId, userId);
    }

    public ClubEventResponse reserveTicket(Long eventId, Long userId) {
        ClubEvent event = requireEvent(eventId);
        membershipPolicy.requireMember(findMembership(userId, event.getClubId()));

        TicketReservationResult result = ticketRepository.reserve(eventId, userId);
        switch (result) {
            case EVENT_NOT_FOUND -> throw new IllegalArgumentException("Event not found");
            case MEMBERSHIP_REQUIRED -> throw new UnauthorizedException("Club membership is required.");
            case ALREADY_RESERVED -> throw new ConflictException("You already have a ticket for this event");
            case SOLD_OUT -> throw new ConflictException("This event is at capacity");
            case RESERVED -> {
                return eventRepository.findResponseById(eventId, userId)
                        .orElseThrow(() -> new DataAccessException("Reserved event ticket could not be loaded"));
            }
            default -> throw new IllegalStateException("Unexpected ticket reservation result");
        }
    }

    public ClubEventResponse cancelTicket(Long eventId, Long userId) {
        ClubEvent event = requireEvent(eventId);
        membershipPolicy.requireMember(findMembership(userId, event.getClubId()));

        if (!ticketRepository.cancel(eventId, userId)) {
            throw new ConflictException("You do not have a ticket for this event");
        }

        return eventRepository.findResponseById(eventId, userId)
                .orElseThrow(() -> new DataAccessException("Updated event could not be loaded"));
    }

    private ClubEvent requireEvent(Long eventId) {
        if (eventId == null) {
            throw new IllegalArgumentException("Event ID is required");
        }
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));
    }

    private Optional<Member> findMembership(Long userId, Long clubId) {
        return memberRepository.findByUserAndClub(userId, clubId);
    }

    private void validateCreateRequest(CreateEventRequest request) {
        if (request.getClubId() == null) {
            throw new IllegalArgumentException("Club ID is required");
        }
        if (isBlank(request.getName()) || request.getName().trim().length() > 100) {
            throw new IllegalArgumentException("Event name is required and must not exceed 100 characters");
        }
        if (isBlank(request.getDescription())) {
            throw new IllegalArgumentException("Event description is required");
        }
        if (isBlank(request.getVenue()) || request.getVenue().trim().length() > 200) {
            throw new IllegalArgumentException("Venue is required and must not exceed 200 characters");
        }
        if (request.getEventTime() == null || !request.getEventTime().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Event time must be in the future");
        }
        if (request.getCapacity() == null || request.getCapacity() < 1) {
            throw new IllegalArgumentException("Event capacity must be at least 1");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
