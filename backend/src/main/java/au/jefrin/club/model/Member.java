package au.jefrin.club.model;

import java.sql.Timestamp;

public class Member {
    private Long id;
    private Long userId;
    private Long clubId;
    private Role role;
    private Timestamp joinedAt;

    public Member() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getClubId() { return clubId; }
    public void setClubId(Long clubId) { this.clubId = clubId; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Timestamp getJoinedAt() { return joinedAt; }
    public void setJoinedAt(Timestamp joinedAt) { this.joinedAt = joinedAt; }
}
