import { useAuth } from '@/features/auth/components/auth-provider';
import { useMembers } from '../api/use-members';
import { MemberTable } from './member-table';
import { useClub } from '@/features/clubs/api/use-club';
import { permissions } from '@/utils/permissions';

type MembersProps = {
  clubId: number;
};

export function Members({ clubId }: MembersProps) {
  const membersQuery = useMembers({ clubId });
  const clubQuery = useClub({ clubId });
  const { user } = useAuth();
  const canManageMembers = !!(
    user &&
    clubQuery.data?.data &&
    clubQuery.data?.data.currentUserRole &&
    permissions.manageMembers(clubQuery.data?.data.currentUserRole)
  );
  return (
    <div className="flex flex-col gap-4 rounded-2xl bg-white p-4">
      <h2 className="text-lg font-semibold">Members</h2>
      <MemberTable
        members={membersQuery.data?.data ?? []}
        isLoading={membersQuery.isLoading || clubQuery.isLoading}
        clubId={clubId}
        canManageMembers={canManageMembers}
      />
    </div>
  );
}
