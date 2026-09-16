package au.jefrin.club.service;

import au.jefrin.club.dto.CreateClubRequest;
import au.jefrin.club.dto.JoinClubRequest;
import au.jefrin.common.exception.ConflictException;
import au.jefrin.club.model.Club;
import au.jefrin.club.model.Member;
import au.jefrin.club.model.Role;
import au.jefrin.club.repository.ClubRepository;
import au.jefrin.club.repository.MemberRepository;

import java.sql.SQLException;

public class ClubService {
    private final ClubRepository clubRepository;
    private final MemberRepository memberRepository;

    public ClubService() {
        this.clubRepository = new ClubRepository();
        this.memberRepository = new MemberRepository();
    }

    public Club createClub(CreateClubRequest request, Long userId) throws SQLException {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Club name is required");
        }

        Club club = new Club();
        club.setName(request.getName());
        club.setDescription(request.getDescription());
        club.setCreatedBy(userId);

        Club savedClub = clubRepository.save(club);

        Member member = new Member();
        member.setUserId(userId);
        member.setClubId(savedClub.getId());
        member.setRole(Role.ADMIN);
        memberRepository.save(member);

        return savedClub;
    }

    public void joinClub(Long clubId, Long userId) throws SQLException {
        if (clubId == null) {
            throw new IllegalArgumentException("Club ID is required");
        }

        Club club = clubRepository.findById(clubId);
        if (club == null) {
            throw new IllegalArgumentException("Club not found");
        }

        if (memberRepository.existsByUserAndClub(userId, clubId)) {
            throw new ConflictException("User is already a member of this club");
        }

        Member member = new Member();
        member.setUserId(userId);
        member.setClubId(clubId);
        member.setRole(Role.MEMBER);
        memberRepository.save(member);
    }


    private void checkAdminPermission(Long clubId, Long requesterUserId) throws SQLException {
        Member requester = memberRepository.findByUserAndClub(requesterUserId, clubId);
        if (requester == null || requester.getRole() != Role.ADMIN) {
            throw new au.jefrin.common.exception.UnauthorizedException("Only club admins can perform this action.");
        }
    }

    public Club editClub(au.jefrin.club.dto.EditClubRequest request, Long requesterUserId) throws SQLException {
        if (request.getClubId() == null || request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Club ID and valid name are required");
        }
        checkAdminPermission(request.getClubId(), requesterUserId);
        
        Club club = clubRepository.findById(request.getClubId());
        if (club == null) {
            throw new IllegalArgumentException("Club not found");
        }
        
        club.setName(request.getName());
        club.setDescription(request.getDescription());
        clubRepository.update(club);
        
        return club;
    }

    public void updateMemberRole(au.jefrin.club.dto.UpdateMemberRoleRequest request, Long requesterUserId) throws SQLException {
        if (request.getClubId() == null || request.getTargetUserId() == null || request.getRole() == null) {
            throw new IllegalArgumentException("Club ID, Target User ID, and Role are required");
        }
        checkAdminPermission(request.getClubId(), requesterUserId);
        
        Member targetMember = memberRepository.findByUserAndClub(request.getTargetUserId(), request.getClubId());
        if (targetMember == null) {
            throw new IllegalArgumentException("Target user is not a member of this club");
        }
        
        if (requesterUserId.equals(request.getTargetUserId())) {
            throw new IllegalArgumentException("Cannot change your own role");
        }
        
        memberRepository.updateRole(request.getTargetUserId(), request.getClubId(), request.getRole());
    }

    public void removeMember(au.jefrin.club.dto.RemoveMemberRequest request, Long requesterUserId) throws SQLException {
        if (request.getClubId() == null || request.getTargetUserId() == null) {
            throw new IllegalArgumentException("Club ID and Target User ID are required");
        }
        checkAdminPermission(request.getClubId(), requesterUserId);
        
        Member targetMember = memberRepository.findByUserAndClub(request.getTargetUserId(), request.getClubId());
        if (targetMember == null) {
            throw new IllegalArgumentException("Target user is not a member of this club");
        }

        if (requesterUserId.equals(request.getTargetUserId())) {
            throw new IllegalArgumentException("Cannot remove yourself using this API");
        }
        
        memberRepository.deleteByUserAndClub(request.getTargetUserId(), request.getClubId());
    }


    public void leaveClub(au.jefrin.club.dto.LeaveClubRequest request, Long requesterUserId) throws SQLException {
        if (request.getClubId() == null) {
            throw new IllegalArgumentException("Club ID is required");
        }
        
        Member membership = memberRepository.findByUserAndClub(requesterUserId, request.getClubId());
        if (membership == null) {
            throw new IllegalArgumentException("You are not a member of this club");
        }
        
        // Optional logic: Prevent the only ADMIN from leaving without transferring ownership
        // For simplicity, we just allow leaving.
        memberRepository.deleteByUserAndClub(requesterUserId, request.getClubId());
    }
}
