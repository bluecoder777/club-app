import { api } from '@/lib/api-client';
import type { MutationConfig } from '@/lib/react-query';
import type { Role } from '@/utils/permissions';
import { useMutation, useQueryClient } from '@tanstack/react-query';

const CHANGE_ROLE_URL = 'clubs/members/role';

type ChangeMemberRoleData = {
  clubId: number;
  targetUserId: number;
  role: Role | string;
};

function changeMemberRole({
  clubId,
  targetUserId,
  role,
}: ChangeMemberRoleData) {
  return api.patch(CHANGE_ROLE_URL, {
    clubId,
    targetUserId,
    role,
  });
}

type UseChangeMemberRole = {
  mutationConfig?: MutationConfig<typeof changeMemberRole>;
};

export const useChangeMemberRole = ({
  mutationConfig,
}: UseChangeMemberRole = {}) => {
  const queryClient = useQueryClient();

  const { onSuccess, ...restConfig } = mutationConfig || {};

  return useMutation({
    onSuccess: (...args) => {
      queryClient.invalidateQueries({
        queryKey: ['club-members'],
      });
      onSuccess?.(...args);
    },
    ...restConfig,
    mutationFn: changeMemberRole,
  });
};
