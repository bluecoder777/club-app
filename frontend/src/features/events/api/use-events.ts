import { api } from '@/lib/api-client';
import type { QueryConfig } from '@/lib/react-query';
import { queryOptions, useQuery } from '@tanstack/react-query';
import { EVENT_ENDPOINTS } from '../config/endpoints';
import type { ClubEvent } from '../types';

export const getEvents = (clubId: number): Promise<{ data: ClubEvent[] }> => {
  return api.get(EVENT_ENDPOINTS.EVENTS_URL, { params: { clubId } });
};

export const getEventsQueryOptions = (clubId: number) =>
  queryOptions({
    queryKey: ['club-events', clubId],
    queryFn: () => getEvents(clubId),
    enabled: !!clubId,
  });

type UseEventsOptions = {
  clubId: number;
  queryConfig?: QueryConfig<typeof getEventsQueryOptions>;
};

export const useEvents = ({ clubId, queryConfig = {} }: UseEventsOptions) =>
  useQuery({
    ...getEventsQueryOptions(clubId),
    ...queryConfig,
  });
