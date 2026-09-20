import { queryOptions, useQuery } from '@tanstack/react-query';

import { api } from '@/lib/api-client';
import type { QueryConfig } from '@/lib/react-query';
import type { Club } from '../types';
import { CLUB_ENDPOINTS } from '../config/endpoints';

export const getClub = (clubId: number): Promise<{ data: Club }> => {
  return api.get(`${CLUB_ENDPOINTS.BASE_URL}/${clubId}`);
};

export const getClubQueryOptions = (clubId: number) => {
  return queryOptions({
    queryKey: ['club', clubId],
    queryFn: () => getClub(clubId),
  });
};

type UseClubOptions = {
  clubId: number;
  queryConfig?: QueryConfig<typeof getClubQueryOptions>;
};

export const useClub = ({ clubId, queryConfig = {} }: UseClubOptions) => {
  return useQuery({
    ...getClubQueryOptions(clubId),
    ...queryConfig,
  });
};
