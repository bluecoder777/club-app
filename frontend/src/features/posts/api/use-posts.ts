import { queryOptions, useQuery } from '@tanstack/react-query';

import { api } from '@/lib/api-client';
import type { QueryConfig } from '@/lib/react-query';
import type { DashboardPost } from '../types';
import { POST_ENDPOINTS } from '../config/endpoints';

export const getPosts = (
  clubId: number,
): Promise<{ data: DashboardPost[] }> => {
  return api.get(POST_ENDPOINTS.DASHBOARD_POSTS_URL, {
    params: { clubId },
  });
};

export const getPostsQueryOptions = (clubId: number) => {
  return queryOptions({
    queryKey: ['dashboard-posts', clubId],
    queryFn: () => getPosts(clubId),
    enabled: !!clubId,
  });
};

type UsePostsOptions = {
  clubId: number;
  queryConfig?: QueryConfig<typeof getPostsQueryOptions>;
};

export const usePosts = ({ clubId, queryConfig = {} }: UsePostsOptions) => {
  return useQuery({
    ...getPostsQueryOptions(clubId),
    ...queryConfig,
  });
};
