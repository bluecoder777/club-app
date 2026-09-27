package au.jefrin.club.service;

import au.jefrin.club.dto.EditClubRequest;
import au.jefrin.club.dto.LeaveClubRequest;
import au.jefrin.club.dto.UpdateMemberRoleRequest;
import au.jefrin.club.dto.RemoveMemberRequest;

import au.jefrin.club.dto.CreateClubRequest;
import au.jefrin.club.dto.ClubResponse;
import au.jefrin.common.exception.ConflictException;
import au.jefrin.common.exception.DataAccessException;
import au.jefrin.club.model.Club;
import au.jefrin.club.model.Member;
import au.jefrin.club.model.Role;
import au.jefrin.club.repository.ClubRepository;
import au.jefrin.club.repository.MemberRepository;
import au.jefrin.club.policy.ClubMembershipPolicy;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import au.jefrin.club.dto.ClubMemberResponse;

public class ClubService {
    private final ClubRepository clubRepository;
    private final MemberRepository memberRepository;
    private final ClubMembershipPolicy membershipPolicy;

    public ClubService(ClubRepository clubRepository,
                       MemberRepository memberRepository,
                       ClubMembershipPolicy membershipPolicy) {
        this.clubRepository = Objects.requireNonNull(clubRepository, "clubRepository must not be null");
        this.memberRepository = Objects.requireNonNull(memberRepository, "memberRepository must not be null");
        this.membershipPolicy = Objects.requireNonNull(membershipPolicy, "membershipPolicy must not be null");
    }

    public ClubResponse createClub(CreateClubRequest request, Long userId) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Club name is required");
        }

        Club club = new Club();
        club.setName(request.getName());
        club.setDescription(request.getDescription());
        club.setCreatedBy(userId);

        Member foundingMember = new Member();
        foundingMember.setUserId(userId);
        foundingMember.setRole(Role.ADMIN);

        Club savedClub = clubRepository.createWithFoundingMember(club, foundingMember);
        
        return clubRepository.findClubResponseById(savedClub.getId(), userId)
                .orElseThrow(() -> new DataAccessException("Created club could not be loaded"));
    }

    public void joinClub(Long clubId, Long userId) {
        if (clubId == null) {
            throw new IllegalArgumentException("Club ID is required");
        }

        if (clubRepository.findById(clubId).isEmpty()) {
            throw new IllegalArgumentException("Club not found");
        }

        if (memberRepository.existsByUserAndClub(userId, clubId)) {
            throw new ConflictException("User is already a member of this club");
        }

        Member member = new Member();
        member.setUserId(userId);
        member.setClubId(clubId);
        member.setRole(membershipPolicy.roleForNewMember(memberRepository.hasMembers(clubId)));
        memberRepository.save(member);
    }


    private void checkAdminPermission(Long clubId, Long requesterUserId) {
        Optional<Member> requester = memberRepository.findByUserAndClub(requesterUserId, clubId);
        membershipPolicy.requireAdmin(requester);
    }

    public ClubResponse editClub(EditClubRequest request, Long requesterUserId) {
        if (request.getClubId() == null || request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Club ID and valid name are required");
        }
        checkAdminPermission(request.getClubId(), requesterUserId);
        
        Club club = clubRepository.findById(request.getClubId())
                .orElseThrow(() -> new IllegalArgumentException("Club not found"));
        
        club.setName(request.getName());
        club.setDescription(request.getDescription());
        clubRepository.update(club);
        
        return clubRepository.findClubResponseById(request.getClubId(), requesterUserId)
                .orElseThrow(() -> new DataAccessException("Updated club could not be loaded"));
    }

    public void updateMemberRole(UpdateMemberRoleRequest request, Long requesterUserId) {
        if (request.getClubId() == null || request.getTargetUserId() == null || request.getRole() == null) {
            throw new IllegalArgumentException("Club ID, Target User ID, and Role are required");
        }
        checkAdminPermission(request.getClubId(), requesterUserId);
        
        Optional<Member> targetMember = memberRepository.findByUserAndClub(
                request.getTargetUserId(),
                request.getClubId()
        );
        if (targetMember.isEmpty()) {
            throw new IllegalArgumentException("Target user is not a member of this club");
        }
        
        if (requesterUserId.equals(request.getTargetUserId())) {
            throw new IllegalArgumentException("Cannot change your own role");
        }
        
        memberRepository.updateRole(request.getTargetUserId(), request.getClubId(), request.getRole());
    }

    public void removeMember(RemoveMemberRequest request, Long requesterUserId) {
        if (request.getClubId() == null || request.getTargetUserId() == null) {
            throw new IllegalArgumentException("Club ID and Target User ID are required");
        }
        checkAdminPermission(request.getClubId(), requesterUserId);
        
        Optional<Member> targetMember = memberRepository.findByUserAndClub(
                request.getTargetUserId(),
                request.getClubId()
        );
        if (targetMember.isEmpty()) {
            throw new IllegalArgumentException("Target user is not a member of this club");
        }

        if (requesterUserId.equals(request.getTargetUserId())) {
            throw new IllegalArgumentException("Cannot remove yourself using this API");
        }
        
        memberRepository.deleteByUserAndClub(request.getTargetUserId(), request.getClubId());
    }


    public void leaveClub(LeaveClubRequest request, Long requesterUserId) {
        if (request.getClubId() == null) {
            throw new IllegalArgumentException("Club ID is required");
        }
        
        Optional<Member> membership = memberRepository.findByUserAndClub(requesterUserId, request.getClubId());
        membershipPolicy.requireMember(membership);
        
        memberRepository.deleteByUserAndClub(requesterUserId, request.getClubId());
    }

    public List<ClubMemberResponse> getClubMembers(Long clubId, Long requesterUserId) {
        if (clubId == null) {
            throw new IllegalArgumentException("Club ID is required");
        }
        
        if (clubRepository.findById(clubId).isEmpty()) {
            throw new IllegalArgumentException("Club not found");
        }
        
        Optional<Member> membership = memberRepository.findByUserAndClub(requesterUserId, clubId);
        membershipPolicy.requireMember(membership);
        
        return memberRepository.findAllMembersByClubId(clubId);
    }

    public List<ClubResponse> getAllClubs(Long userId) {
        return clubRepository.findAllClubResponses(userId);
    }

    public ClubResponse getClub(Long clubId, Long userId) {
        if (clubId == null) {
            throw new IllegalArgumentException("Club ID is required");
        }
        return clubRepository.findClubResponseById(clubId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Club not found"));
    }
}
