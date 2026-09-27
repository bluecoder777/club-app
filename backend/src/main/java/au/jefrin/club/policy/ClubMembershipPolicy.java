package au.jefrin.club.policy;

import au.jefrin.club.model.Member;
import au.jefrin.club.model.Role;
import au.jefrin.common.exception.UnauthorizedException;

import java.util.Optional;

public final class ClubMembershipPolicy {

    public Role roleForNewMember(boolean clubHasMembers) {
        return clubHasMembers ? Role.MEMBER : Role.ADMIN;
    }

    public void requireAdmin(Optional<Member> member) {
        if (member.isEmpty() || member.get().getRole() != Role.ADMIN) {
            throw new UnauthorizedException("Only club admins can perform this action.");
        }
    }

    public void requireMember(Optional<Member> member) {
        if (member.isEmpty()) {
            throw new UnauthorizedException("Club membership is required.");
        }
    }
}
