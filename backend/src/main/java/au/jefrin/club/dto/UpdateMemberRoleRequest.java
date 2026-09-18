package au.jefrin.club.dto;

import au.jefrin.club.model.Role;

public class UpdateMemberRoleRequest {
    private Long clubId;
    private Long targetUserId;
    private Role role;

    public UpdateMemberRoleRequest() {}

    public Long getClubId() { return clubId; }
    public void setClubId(Long clubId) { this.clubId = clubId; }

    public Long getTargetUserId() { return targetUserId; }
    public void setTargetUserId(Long targetUserId) { this.targetUserId = targetUserId; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
