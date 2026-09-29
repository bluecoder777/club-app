package au.jefrin.club.dto;

public class LeaveClubRequest {
    private Long clubId;
    private Long successorUserId;

    public LeaveClubRequest() {
    }

    public Long getClubId() {
        return clubId;
    }

    public void setClubId(Long clubId) {
        this.clubId = clubId;
    }

    public Long getSuccessorUserId() {
        return successorUserId;
    }

    public void setSuccessorUserId(Long successorUserId) {
        this.successorUserId = successorUserId;
    }
}
