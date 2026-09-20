import { queryOptions, useQuery } from '@tanstack/react-query';

import { api } from '@/lib/api-client';
import type { QueryConfig } from '@/lib/react-query';
import type { Club } from '../types';
import { CLUB_ENDPOINTS } from '../config/endpoints';

export const getClubs = (): Promise<{ data: Club[] }> => {
  return api.get(CLUB_ENDPOINTS.BASE_URL);
};

export const getClubsQueryOptions = () => {
  return queryOptions({
    queryKey: ['clubs'],
    queryFn: () => getClubs(),
  });
};

type UseClubsOptions = {
  queryConfig?: QueryConfig<typeof getClubsQueryOptions>;
};

export const useClubs = ({ queryConfig = {} }: UseClubsOptions = {}) => {
  return useQuery({
    ...getClubsQueryOptions(),
    ...queryConfig,
  });
};
