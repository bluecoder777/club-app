package au.jefrin.club.service;

import au.jefrin.club.dto.ClubMemberResponse;
import au.jefrin.club.dto.ClubResponse;
import au.jefrin.club.dto.LeaveClubRequest;
import au.jefrin.club.model.Club;
import au.jefrin.club.model.Member;
import au.jefrin.club.model.Role;
import au.jefrin.club.policy.ClubMembershipPolicy;
import au.jefrin.club.repository.ClubRepository;
import au.jefrin.club.repository.MemberRepository;
import au.jefrin.common.exception.ConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClubServiceTest {
    private static final Long CLUB_ID = 10L;
    private static final Long ADMIN_ID = 1L;
    private static final Long MEMBER_ID = 2L;

    private FakeMemberRepository memberRepository;
    private ClubService service;

    @BeforeEach
    void setUp() {
        memberRepository = new FakeMemberRepository();
        service = new ClubService(
                new UnusedClubRepository(),
                memberRepository,
                new ClubMembershipPolicy()
        );
    }

    @Test
    void adminMustChooseASuccessorWhenOtherMembersRemain() {
        memberRepository.add(ADMIN_ID, Role.ADMIN);
        memberRepository.add(MEMBER_ID, Role.MEMBER);

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> service.leaveClub(leaveRequest(null), ADMIN_ID)
        );

        assertEquals("ADMIN_SUCCESSOR_REQUIRED", exception.getErrorKey());
        assertTrue(memberRepository.contains(ADMIN_ID));
        assertFalse(memberRepository.handoverCompleted);
    }

    @Test
    void chosenMemberBecomesAdminWhenCurrentAdminLeaves() {
        memberRepository.add(ADMIN_ID, Role.ADMIN);
        memberRepository.add(MEMBER_ID, Role.MEMBER);

        service.leaveClub(leaveRequest(MEMBER_ID), ADMIN_ID);

        assertFalse(memberRepository.contains(ADMIN_ID));
        assertEquals(Role.ADMIN, memberRepository.roleOf(MEMBER_ID));
        assertTrue(memberRepository.handoverCompleted);
    }

    @Test
    void soleAdminCanLeaveWithoutChoosingASuccessor() {
        memberRepository.add(ADMIN_ID, Role.ADMIN);

        service.leaveClub(leaveRequest(null), ADMIN_ID);

        assertFalse(memberRepository.contains(ADMIN_ID));
        assertFalse(memberRepository.handoverCompleted);
    }

    private LeaveClubRequest leaveRequest(Long successorUserId) {
        LeaveClubRequest request = new LeaveClubRequest();
        request.setClubId(CLUB_ID);
        request.setSuccessorUserId(successorUserId);
        return request;
    }

    private static class FakeMemberRepository implements MemberRepository {
        private final Map<Long, Role> members = new LinkedHashMap<>();
        private boolean handoverCompleted;

        void add(Long userId, Role role) {
            members.put(userId, role);
        }

        boolean contains(Long userId) {
            return members.containsKey(userId);
        }

        Role roleOf(Long userId) {
            return members.get(userId);
        }

        @Override
        public Member save(Member member) {
            members.put(member.getUserId(), member.getRole());
            return member;
        }

        @Override
        public boolean existsByUserAndClub(Long userId, Long clubId) {
            return members.containsKey(userId);
        }

        @Override
        public boolean hasMembers(Long clubId) {
            return !members.isEmpty();
        }

        @Override
        public Optional<Member> findByUserAndClub(Long userId, Long clubId) {
            Role role = members.get(userId);
            if (role == null) {
                return Optional.empty();
            }

            Member member = new Member();
            member.setUserId(userId);
            member.setClubId(clubId);
            member.setRole(role);
            return Optional.of(member);
        }

        @Override
        public List<ClubMemberResponse> findAllMembersByClubId(Long clubId) {
            return members.entrySet().stream()
                    .map(entry -> ClubMemberResponse.builder()
                            .userId(entry.getKey())
                            .role(entry.getValue())
                            .build())
                    .toList();
        }

        @Override
        public void updateRole(Long userId, Long clubId, Role role) {
            members.put(userId, role);
        }

        @Override
        public void handOverAdministrationAndRemove(Long departingUserId, Long successorUserId, Long clubId) {
            members.put(successorUserId, Role.ADMIN);
            members.remove(departingUserId);
            handoverCompleted = true;
        }

        @Override
        public void deleteByUserAndClub(Long userId, Long clubId) {
            members.remove(userId);
        }
    }

    private static class UnusedClubRepository implements ClubRepository {
        @Override
        public Club createWithFoundingMember(Club club, Member foundingMember) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<Club> findById(Long id) {
            return Optional.empty();
        }

        @Override
        public void update(Club club) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<ClubResponse> findClubResponseById(Long id, Long currentUserId) {
            return Optional.empty();
        }

        @Override
        public List<ClubResponse> findAllClubResponses(Long currentUserId) {
            return List.of();
        }
    }
}
