import { api } from '@/lib/api-client';
import type { MutationConfig } from '@/lib/react-query';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { EVENT_ENDPOINTS } from '../config/endpoints';
import { getEventsQueryOptions } from './use-events';

function reserveTicket(eventId: number) {
  return api.post(EVENT_ENDPOINTS.TICKETS_URL, { eventId });
}

type UseReserveTicketOptions = {
  clubId: number;
  mutationConfig?: MutationConfig<typeof reserveTicket>;
};

export const useReserveTicket = ({
  clubId,
  mutationConfig,
}: UseReserveTicketOptions) => {
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
    mutationFn: reserveTicket,
  });
};
