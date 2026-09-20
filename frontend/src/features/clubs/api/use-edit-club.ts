import { api } from '@/lib/api-client';
import type { MutationConfig } from '@/lib/react-query';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { CLUB_ENDPOINTS } from '../config/endpoints';
import type { ClubFormData } from '../schema/club';
import type { Club } from '../types';
import { getClubQueryOptions } from './use-club';
import { getClubsQueryOptions } from './use-clubs';

export type EditClubData = {
  data: ClubFormData;
  clubId: number;
};

function editClub({ clubId, data }: EditClubData): Promise<{ data: Club }> {
  return api.patch(CLUB_ENDPOINTS.EDIT_URL, {
    clubId,
    ...data,
  });
}

type UseEditClubOptions = {
  mutationConfig?: MutationConfig<typeof editClub>;
  clubId: number;
};

export const useEditClub = ({ mutationConfig, clubId }: UseEditClubOptions) => {
  const queryClient = useQueryClient();

  const { onSuccess, ...restConfig } = mutationConfig || {};
  return useMutation({
    onSuccess: (...args) => {
      queryClient.invalidateQueries({
        queryKey: getClubQueryOptions(clubId).queryKey,
      });
      queryClient.invalidateQueries({
        queryKey: getClubsQueryOptions().queryKey,
      });
      onSuccess?.(...args);
    },
    ...restConfig,
    mutationFn: editClub,
  });
};
