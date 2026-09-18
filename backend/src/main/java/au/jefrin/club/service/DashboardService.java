package au.jefrin.club.service;

import au.jefrin.club.dto.CreatePostRequest;
import au.jefrin.club.dto.DashboardPostResponse;
import au.jefrin.club.dto.EditPostRequest;
import au.jefrin.club.model.Club;
import au.jefrin.club.model.DashboardPost;
import au.jefrin.club.model.Member;
import au.jefrin.club.model.Role;
import au.jefrin.club.repository.ClubRepository;
import au.jefrin.club.repository.DashboardPostRepository;
import au.jefrin.club.repository.MemberRepository;
import au.jefrin.common.exception.UnauthorizedException;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class DashboardService {
    private final DashboardPostRepository dashboardPostRepository;
    private final MemberRepository memberRepository;
    private final ClubRepository clubRepository;

    public DashboardService() {
        this.dashboardPostRepository = new DashboardPostRepository();
        this.memberRepository = new MemberRepository();
        this.clubRepository = new ClubRepository();
    }

    private void checkAdminPermission(Long clubId, Long requesterUserId) throws SQLException {
        Member requester = memberRepository.findByUserAndClub(requesterUserId, clubId);
        if (requester == null || requester.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("Only club admins can perform this action.");
        }
    }

    private void checkMemberPermission(Long clubId, Long requesterUserId) throws SQLException {
        Member requester = memberRepository.findByUserAndClub(requesterUserId, clubId);
        if (requester == null) {
            throw new UnauthorizedException("Only club members can view dashboard posts.");
        }
    }

    public DashboardPostResponse createPost(CreatePostRequest request, Long userId) throws SQLException {
        if (request.getClubId() == null || request.getTitle() == null || request.getTitle().trim().isEmpty() ||
            request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Club ID, title, and description are required");
        }

        Club club = clubRepository.findById(request.getClubId());
        if (club == null) {
            throw new IllegalArgumentException("Club not found");
        }

        checkAdminPermission(request.getClubId(), userId);

        DashboardPost post = new DashboardPost();
        post.setClubId(request.getClubId());
        post.setTitle(request.getTitle());
        post.setDescription(request.getDescription());
        post.setCreatedBy(userId);
        post.setLastUpdatedBy(userId);

        DashboardPost savedPost = dashboardPostRepository.save(post);
        return dashboardPostRepository.findPostResponseById(savedPost.getId());
    }

    public DashboardPostResponse editPost(EditPostRequest request, Long userId) throws SQLException {
        if (request.getPostId() == null || request.getTitle() == null || request.getTitle().trim().isEmpty() ||
            request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Post ID, title, and description are required");
        }

        DashboardPost post = dashboardPostRepository.findById(request.getPostId());
        if (post == null) {
            throw new IllegalArgumentException("Dashboard post not found");
        }

        checkAdminPermission(post.getClubId(), userId);

        post.setTitle(request.getTitle());
        post.setDescription(request.getDescription());
        post.setLastUpdatedBy(userId);

        dashboardPostRepository.update(post);
        
        // Fetch again to get updated details (including user data)
        return dashboardPostRepository.findPostResponseById(request.getPostId());
    }

    public List<DashboardPostResponse> listPosts(Long clubId, Long userId) throws SQLException {
        if (clubId == null) {
            throw new IllegalArgumentException("Club ID is required");
        }

        Club club = clubRepository.findById(clubId);
        if (club == null) {
            throw new IllegalArgumentException("Club not found");
        }

        checkMemberPermission(clubId, userId);

        return dashboardPostRepository.findAllPostResponsesByClubId(clubId);
    }
}

