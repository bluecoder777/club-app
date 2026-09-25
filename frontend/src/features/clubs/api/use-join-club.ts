import { api } from '@/lib/api-client';
import type { MutationConfig } from '@/lib/react-query';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { CLUB_ENDPOINTS } from '../config/endpoints';
import { getClubQueryOptions } from './use-club';
import { getClubsQueryOptions } from './use-clubs';
import { getMembersQueryOptions } from '@/features/members/api/use-members';

type JoinClubData = {
  clubId: number;
};

function joinClub(data: JoinClubData) {
  return api.post(CLUB_ENDPOINTS.JOIN_URL, data);
}

type UseJoinClub = {
  mutationConfig?: MutationConfig<typeof joinClub>;
  clubId: number;
};

export const useJoinClub = ({ mutationConfig, clubId }: UseJoinClub) => {
  const queryClient = useQueryClient();
  const { onSuccess, ...restConfig } = mutationConfig || {};

  return useMutation({
    ...restConfig,
    onSuccess: (...args) => {
      queryClient.invalidateQueries({
        queryKey: getClubQueryOptions(clubId).queryKey,
      });
      queryClient.invalidateQueries({
        queryKey: getClubsQueryOptions().queryKey,
      });
      queryClient.invalidateQueries({
        queryKey: getMembersQueryOptions(clubId).queryKey,
      });
      onSuccess?.(...args);
    },
    mutationFn: joinClub,
  });
};
