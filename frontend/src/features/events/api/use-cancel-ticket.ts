import { api } from '@/lib/api-client';
import type { MutationConfig } from '@/lib/react-query';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { EVENT_ENDPOINTS } from '../config/endpoints';
import { getEventsQueryOptions } from './use-events';

function cancelTicket(eventId: number) {
  return api.delete(EVENT_ENDPOINTS.TICKETS_URL, { data: { eventId } });
}

type UseCancelTicketOptions = {
  clubId: number;
  mutationConfig?: MutationConfig<typeof cancelTicket>;
};

export const useCancelTicket = ({
  clubId,
  mutationConfig,
}: UseCancelTicketOptions) => {
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
    mutationFn: cancelTicket,
  });
};
