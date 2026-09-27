package au.jefrin.club.service;

import au.jefrin.club.dto.CreatePostRequest;
import au.jefrin.club.dto.DashboardPostResponse;
import au.jefrin.club.dto.EditPostRequest;
import au.jefrin.club.model.Club;
import au.jefrin.club.model.DashboardPost;
import au.jefrin.club.model.Member;
import au.jefrin.club.repository.ClubRepository;
import au.jefrin.club.repository.DashboardPostRepository;
import au.jefrin.club.repository.MemberRepository;
import au.jefrin.club.policy.ClubMembershipPolicy;
import au.jefrin.common.exception.DataAccessException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class DashboardService {
    private final DashboardPostRepository dashboardPostRepository;
    private final MemberRepository memberRepository;
    private final ClubRepository clubRepository;
    private final ClubMembershipPolicy membershipPolicy;

    public DashboardService(DashboardPostRepository dashboardPostRepository,
                            MemberRepository memberRepository,
                            ClubRepository clubRepository,
                            ClubMembershipPolicy membershipPolicy) {
        this.dashboardPostRepository = Objects.requireNonNull(
                dashboardPostRepository,
                "dashboardPostRepository must not be null"
        );
        this.memberRepository = Objects.requireNonNull(memberRepository, "memberRepository must not be null");
        this.clubRepository = Objects.requireNonNull(clubRepository, "clubRepository must not be null");
        this.membershipPolicy = Objects.requireNonNull(membershipPolicy, "membershipPolicy must not be null");
    }

    private void checkAdminPermission(Long clubId, Long requesterUserId) {
        Optional<Member> requester = memberRepository.findByUserAndClub(requesterUserId, clubId);
        membershipPolicy.requireAdmin(requester);
    }

    private void checkMemberPermission(Long clubId, Long requesterUserId) {
        Optional<Member> requester = memberRepository.findByUserAndClub(requesterUserId, clubId);
        membershipPolicy.requireMember(requester);
    }

    public DashboardPostResponse createPost(CreatePostRequest request, Long userId) {
        if (request.getClubId() == null || request.getTitle() == null || request.getTitle().trim().isEmpty() ||
            request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Club ID, title, and description are required");
        }

        if (clubRepository.findById(request.getClubId()).isEmpty()) {
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
        return dashboardPostRepository.findPostResponseById(savedPost.getId())
                .orElseThrow(() -> new DataAccessException("Created dashboard post could not be loaded"));
    }

    public DashboardPostResponse editPost(EditPostRequest request, Long userId) {
        if (request.getPostId() == null || request.getTitle() == null || request.getTitle().trim().isEmpty() ||
            request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Post ID, title, and description are required");
        }

        DashboardPost post = dashboardPostRepository.findById(request.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("Dashboard post not found"));

        checkAdminPermission(post.getClubId(), userId);

        post.setTitle(request.getTitle());
        post.setDescription(request.getDescription());
        post.setLastUpdatedBy(userId);

        dashboardPostRepository.update(post);
        
        return dashboardPostRepository.findPostResponseById(request.getPostId())
                .orElseThrow(() -> new DataAccessException("Updated dashboard post could not be loaded"));
    }

    public List<DashboardPostResponse> listPosts(Long clubId, Long userId) {
        if (clubId == null) {
            throw new IllegalArgumentException("Club ID is required");
        }

        if (clubRepository.findById(clubId).isEmpty()) {
            throw new IllegalArgumentException("Club not found");
        }

        checkMemberPermission(clubId, userId);

        return dashboardPostRepository.findAllPostResponsesByClubId(clubId);
    }
}

