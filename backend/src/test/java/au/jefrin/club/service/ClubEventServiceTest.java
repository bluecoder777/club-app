package au.jefrin.club.service;

import au.jefrin.club.dto.ClubEventResponse;
import au.jefrin.club.dto.ClubMemberResponse;
import au.jefrin.club.dto.ClubResponse;
import au.jefrin.club.dto.CreateEventRequest;
import au.jefrin.club.model.*;
import au.jefrin.club.policy.ClubMembershipPolicy;
import au.jefrin.club.repository.ClubEventRepository;
import au.jefrin.club.repository.ClubRepository;
import au.jefrin.club.repository.EventTicketRepository;
import au.jefrin.club.repository.MemberRepository;
import au.jefrin.common.exception.ConflictException;
import au.jefrin.common.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ClubEventServiceTest {
    private FakeEventRepository eventRepository;
    private FakeTicketRepository ticketRepository;
    private FakeMemberRepository memberRepository;
    private ClubEventService service;

    @BeforeEach
    void setUp() {
        eventRepository = new FakeEventRepository();
        ticketRepository = new FakeTicketRepository(eventRepository);
        memberRepository = new FakeMemberRepository(Role.ADMIN);
        service = new ClubEventService(
                eventRepository,
                ticketRepository,
                new FakeClubRepository(),
                memberRepository,
                new ClubMembershipPolicy()
        );
    }

    @Test
    void adminCanCreateAnEvent() {
        CreateEventRequest request = validCreateRequest();
        request.setName("  Annual Dinner  ");

        ClubEventResponse response = service.createEvent(request, 7L);

        assertEquals("Annual Dinner", response.getName());
        assertEquals(40, response.getCapacity());
        assertEquals(7L, eventRepository.savedEvent.getCreatedBy());
    }

    @Test
    void regularMemberCannotCreateAnEvent() {
        memberRepository.role = Role.MEMBER;

        assertThrows(UnauthorizedException.class, () -> service.createEvent(validCreateRequest(), 7L));
    }

    @Test
    void soldOutEventRejectsAnotherTicket() {
        eventRepository.savedEvent = existingEvent();
        memberRepository.role = Role.MEMBER;
        ticketRepository.result = TicketReservationResult.SOLD_OUT;

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> service.reserveTicket(10L, 7L)
        );

        assertEquals("This event is at capacity", exception.getMessage());
    }

    @Test
    void memberCannotReserveASecondTicket() {
        eventRepository.savedEvent = existingEvent();
        memberRepository.role = Role.MEMBER;
        ticketRepository.result = TicketReservationResult.ALREADY_RESERVED;

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> service.reserveTicket(10L, 7L)
        );

        assertEquals("You already have a ticket for this event", exception.getMessage());
    }

    @Test
    void memberCanReserveAndCancelOneTicket() {
        eventRepository.savedEvent = existingEvent();
        memberRepository.role = Role.MEMBER;

        ClubEventResponse reserved = service.reserveTicket(10L, 7L);
        ClubEventResponse cancelled = service.cancelTicket(10L, 7L);

        assertTrue(reserved.isHasTicket());
        assertEquals(1, reserved.getTicketsIssued());
        assertFalse(cancelled.isHasTicket());
        assertEquals(0, cancelled.getTicketsIssued());
    }

    private CreateEventRequest validCreateRequest() {
        CreateEventRequest request = new CreateEventRequest();
        request.setClubId(1L);
        request.setName("Annual Dinner");
        request.setDescription("Club dinner and awards");
        request.setVenue("Main Hall");
        request.setEventTime(LocalDateTime.now().plusDays(2));
        request.setCapacity(40);
        return request;
    }

    private ClubEvent existingEvent() {
        ClubEvent event = new ClubEvent();
        event.setId(10L);
        event.setClubId(1L);
        event.setName("Annual Dinner");
        event.setDescription("Club dinner and awards");
        event.setVenue("Main Hall");
        event.setEventTime(LocalDateTime.now().plusDays(2));
        event.setCapacity(40);
        event.setCreatedBy(3L);
        return event;
    }

    private static class FakeEventRepository implements ClubEventRepository {
        private ClubEvent savedEvent;
        private boolean hasTicket;
        private int ticketsIssued;

        @Override
        public ClubEvent save(ClubEvent event) {
            event.setId(10L);
            savedEvent = event;
            return event;
        }

        @Override
        public Optional<ClubEvent> findById(Long eventId) {
            return savedEvent != null && savedEvent.getId().equals(eventId)
                    ? Optional.of(savedEvent)
                    : Optional.empty();
        }

        @Override
        public Optional<ClubEventResponse> findResponseById(Long eventId, Long currentUserId) {
            return findById(eventId).map(event -> ClubEventResponse.builder()
                    .id(event.getId())
                    .clubId(event.getClubId())
                    .name(event.getName())
                    .description(event.getDescription())
                    .venue(event.getVenue())
                    .eventTime(event.getEventTime())
                    .capacity(event.getCapacity())
                    .ticketsIssued(ticketsIssued)
                    .hasTicket(hasTicket)
                    .build());
        }

        @Override
        public List<ClubEventResponse> findAllResponsesByClubId(Long clubId, Long currentUserId) {
            return findResponseById(savedEvent.getId(), currentUserId).stream().toList();
        }
    }

    private static class FakeTicketRepository implements EventTicketRepository {
        private final FakeEventRepository eventRepository;
        private TicketReservationResult result = TicketReservationResult.RESERVED;

        private FakeTicketRepository(FakeEventRepository eventRepository) {
            this.eventRepository = eventRepository;
        }

        @Override
        public TicketReservationResult reserve(Long eventId, Long userId) {
            if (result == TicketReservationResult.RESERVED) {
                eventRepository.hasTicket = true;
                eventRepository.ticketsIssued = 1;
            }
            return result;
        }

        @Override
        public boolean cancel(Long eventId, Long userId) {
            if (!eventRepository.hasTicket) {
                return false;
            }
            eventRepository.hasTicket = false;
            eventRepository.ticketsIssued = 0;
            return true;
        }
    }

    private static class FakeMemberRepository implements MemberRepository {
        private Role role;

        private FakeMemberRepository(Role role) {
            this.role = role;
        }

        @Override
        public Optional<Member> findByUserAndClub(Long userId, Long clubId) {
            Member member = new Member();
            member.setUserId(userId);
            member.setClubId(clubId);
            member.setRole(role);
            return Optional.of(member);
        }

        @Override
        public Member save(Member member) {
            return member;
        }

        @Override
        public boolean existsByUserAndClub(Long userId, Long clubId) {
            return true;
        }

        @Override
        public boolean hasMembers(Long clubId) {
            return true;
        }

        @Override
        public List<ClubMemberResponse> findAllMembersByClubId(Long clubId) {
            return List.of();
        }

        @Override
        public void updateRole(Long userId, Long clubId, Role role) {
        }

        @Override
        public void handOverAdministrationAndRemove(Long departingUserId, Long successorUserId, Long clubId) {
        }

        @Override
        public void deleteByUserAndClub(Long userId, Long clubId) {
        }
    }

    private static class FakeClubRepository implements ClubRepository {
        @Override
        public Optional<Club> findById(Long id) {
            Club club = new Club();
            club.setId(id);
            return Optional.of(club);
        }

        @Override
        public Club createWithFoundingMember(Club club, Member foundingMember) {
            return club;
        }

        @Override
        public void update(Club club) {
        }

        @Override
        public Optional<ClubResponse> findClubResponseById(Long id, Long userId) {
            return Optional.empty();
        }

        @Override
        public List<ClubResponse> findAllClubResponses(Long userId) {
            return List.of();
        }
    }
}
