import { api } from '@/lib/api-client';
import type { MutationConfig } from '@/lib/react-query';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { CLUB_ENDPOINTS } from '../config/endpoints';
import type { ClubFormData } from '../schema/club';
import { getClubsQueryOptions } from './use-clubs';
import type { Club } from '../types';

function createClub(data: ClubFormData): Promise<{ data: Club }> {
  return api.post(CLUB_ENDPOINTS.BASE_URL, data);
}

type UseCreateClubOptions = {
  mutationConfig?: MutationConfig<typeof createClub>;
};

export const useCreateClub = ({
  mutationConfig,
}: UseCreateClubOptions = {}) => {
  const queryClient = useQueryClient();

  const { onSuccess, ...restConfig } = mutationConfig || {};

  return useMutation({
    onSuccess: (...args) => {
      queryClient.invalidateQueries({
        queryKey: getClubsQueryOptions().queryKey,
      });
      onSuccess?.(...args);
    },
    ...restConfig,
    mutationFn: createClub,
  });
};
