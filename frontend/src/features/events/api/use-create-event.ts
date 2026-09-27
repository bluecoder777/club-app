import { api } from '@/lib/api-client';
import type { MutationConfig } from '@/lib/react-query';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { EVENT_ENDPOINTS } from '../config/endpoints';
import type { EventFormData } from '../schema/event';
import { getEventsQueryOptions } from './use-events';

type CreateEventData = EventFormData & { clubId: number };

function createEvent(data: CreateEventData) {
  return api.post(EVENT_ENDPOINTS.EVENTS_URL, data);
}

type UseCreateEventOptions = {
  clubId: number;
  mutationConfig?: MutationConfig<typeof createEvent>;
};

export const useCreateEvent = ({
  clubId,
  mutationConfig,
}: UseCreateEventOptions) => {
  const queryClient = useQueryClient();
  const { onSuccess, ...restConfig } = mutationConfig || {};

  return useMutation({
    onSuccess: (...args) => {
      queryClient.invalidateQueries({
        queryKey: getEventsQueryOptions(clubId).queryKey,
      });
      onSuccess?.(...args);
    },
    ...restConfig,
    mutationFn: createEvent,
  });
};
