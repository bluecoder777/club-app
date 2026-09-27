import { Button } from '@/components/ui/button';
import { toast } from '@/components/ui/toast';
import { getErrorMessage } from '@/utils/get-error-message';
import { useCancelTicket } from '../api/use-cancel-ticket';
import { useReserveTicket } from '../api/use-reserve-ticket';
import type { ClubEvent } from '../types';

type TicketActionProps = {
  event: ClubEvent;
};

export function TicketAction({ event }: TicketActionProps) {
  const reserveTicket = useReserveTicket({
    clubId: event.clubId,
    mutationConfig: {
      onSuccess: () =>
        toast.add({ title: 'Ticket reserved!', type: 'success' }),
      onError: (error) =>
        toast.add({
          title: getErrorMessage(error, 'Unable to reserve ticket'),
          type: 'error',
        }),
    },
  });

  const cancelTicket = useCancelTicket({
    clubId: event.clubId,
    mutationConfig: {
      onSuccess: () =>
        toast.add({ title: 'Ticket cancelled', type: 'success' }),
      onError: (error) =>
        toast.add({
          title: getErrorMessage(error, 'Unable to cancel ticket'),
          type: 'error',
        }),
    },
  });

  if (event.hasTicket) {
    return (
      <Button
        variant="outline"
        size="sm"
        disabled={cancelTicket.isPending}
        onClick={() => cancelTicket.mutate(event.id)}
      >
        {cancelTicket.isPending ? 'Cancelling...' : 'Cancel ticket'}
      </Button>
    );
  }

  const isFull = event.ticketsIssued >= event.capacity;
  return (
    <Button
      size="sm"
      disabled={isFull || reserveTicket.isPending}
      onClick={() => reserveTicket.mutate(event.id)}
    >
      {reserveTicket.isPending
        ? 'Reserving...'
        : isFull
          ? 'Sold out'
          : 'Get ticket'}
    </Button>
  );
}
