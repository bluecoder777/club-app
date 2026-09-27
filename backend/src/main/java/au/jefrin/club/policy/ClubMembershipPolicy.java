package au.jefrin.club.policy;

import au.jefrin.club.model.Member;
import au.jefrin.club.model.Role;
import au.jefrin.common.exception.UnauthorizedException;

public final class ClubMembershipPolicy {

    public Role roleForNewMember(boolean clubHasMembers) {
        return clubHasMembers ? Role.MEMBER : Role.ADMIN;
    }

    public void requireAdmin(Member member) {
        if (member == null || member.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("Only club admins can perform this action.");
        }
    }

    public void requireMember(Member member) {
        if (member == null) {
            throw new UnauthorizedException("Club membership is required.");
        }
    }
}
