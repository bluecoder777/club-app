import { api } from '@/lib/api-client';
import type { MutationConfig } from '@/lib/react-query';
import { useMutation } from '@tanstack/react-query';

const REMOVE_MEMBER_URL = 'clubs/members';

type RemoveClubMemberData = {
  clubId: number;
  targetUserId: number;
};

function removeClubMember({ clubId, targetUserId }: RemoveClubMemberData) {
  return api.delete(`${REMOVE_MEMBER_URL}/${clubId}/${targetUserId}`);
}

type UseRemoveClubMember = {
  mutationConfig?: MutationConfig<typeof removeClubMember>;
};

export const useRemoveClubMember = ({
  mutationConfig,
}: UseRemoveClubMember = {}) => {
  return useMutation({
    mutationFn: removeClubMember,
    ...mutationConfig,
  });
};
