import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { toast } from '@/components/ui/toast';
import { useChangeMemberRole } from '../api/use-change-member-role';

type MemberRoleProps = {
  clubId: number;
  targetUserId: number;
  role: string;
  canManageMembers: boolean;
};

const ROLES = [
  {
    value: 'MEMBER',
    label: 'Member',
  },
  {
    value: 'ADMIN',
    label: 'Admin',
  },
] as const;

export default function MemberRole({
  clubId,
  targetUserId,
  role,
  canManageMembers,
}: MemberRoleProps) {
  const changeRoleMutation = useChangeMemberRole({
    mutationConfig: {
      onSuccess: () => {
        toast.add({
          title: 'Role updated',
          description: 'Member role has been updated successfully.',
        });
      },
      onError: () => {
        toast.add({
          title: 'Failed to update role',
          description: 'Something went wrong. Please try again.',
          type: 'error',
        });
      },
    },
  });

  const handleRoleChange = (newRole: string) => {
    if (newRole === role) return;

    changeRoleMutation.mutate({
      clubId,
      targetUserId,
      role: newRole,
    });
  };

  return (
    <Select
      value={role}
      onValueChange={(val) => {
        val && handleRoleChange(val);
      }}
      disabled={changeRoleMutation.isPending || !canManageMembers}
    >
      <SelectTrigger className="w-28">
        <SelectValue />
      </SelectTrigger>

      <SelectContent>
        {ROLES.map((item) => (
          <SelectItem key={item.value} value={item.value}>
            {item.label}
          </SelectItem>
        ))}
      </SelectContent>
    </Select>
  );
}
