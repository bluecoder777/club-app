package au.jefrin.club.dto;

public class RemoveMemberRequest {
    private Long clubId;
    private Long targetUserId;

    public RemoveMemberRequest() {}

    public Long getClubId() { return clubId; }
    public void setClubId(Long clubId) { this.clubId = clubId; }

    public Long getTargetUserId() { return targetUserId; }
    public void setTargetUserId(Long targetUserId) { this.targetUserId = targetUserId; }
}
