import { queryOptions, useQuery } from '@tanstack/react-query';
import { api } from '@/lib/api-client';
import type { QueryConfig } from '@/lib/react-query';
import type { ClubMember } from '../types';

const MEMBERS_URL = 'clubs/members';

export const getMembers = (clubId: number): Promise<{ data: ClubMember[] }> => {
  return api.get(MEMBERS_URL, {
    params: { clubId },
  });
};

export const getMembersQueryOptions = (clubId: number) => {
  return queryOptions({
    queryKey: ['club-members', clubId],
    queryFn: () => getMembers(clubId),
    enabled: !!clubId,
  });
};

type UseMembersOptions = {
  clubId: number;
  queryConfig?: QueryConfig<typeof getMembersQueryOptions>;
};

export const useMembers = ({ clubId, queryConfig = {} }: UseMembersOptions) => {
  return useQuery({
    ...getMembersQueryOptions(clubId),
    ...queryConfig,
  });
};
