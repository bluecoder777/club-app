import { Badge } from '@/components/ui/badge';
import {
  Empty,
  EmptyDescription,
  EmptyHeader,
  EmptyTitle,
} from '@/components/ui/empty';
import { Skeleton } from '@/components/ui/skeleton';
import { formatDate } from '@/utils/forma-date';
import { CalendarDays, MapPin, Ticket } from 'lucide-react';
import type { ClubEvent } from '../types';
import { TicketAction } from './ticket-action';

type EventListProps = {
  events: ClubEvent[];
};

export function EventListSkeleton() {
  return (
    <div className="grid gap-4 md:grid-cols-2">
      {Array.from({ length: 2 }).map((_, index) => (
        <div key={index} className="bg-muted/30 rounded-xl p-4">
          <Skeleton className="mb-3 h-5 w-48" />
          <Skeleton className="mb-2 h-4 w-36" />
          <Skeleton className="h-4 w-full" />
        </div>
      ))}
    </div>
  );
}

export function EventList({ events }: EventListProps) {
  if (!events.length) {
    return (
      <Empty className="min-h-48 rounded-xl border">
        <EmptyHeader>
          <CalendarDays className="text-muted-foreground size-10" />
          <EmptyTitle>No events yet</EmptyTitle>
          <EmptyDescription>
            There are no upcoming club events to show.
          </EmptyDescription>
        </EmptyHeader>
      </Empty>
    );
  }

  return (
    <div className="grid gap-4 md:grid-cols-2">
      {events.map((event) => (
        <article
          key={event.id}
          className="bg-muted/30 flex flex-col gap-4 rounded-xl p-4"
        >
          <div className="flex items-start justify-between gap-3">
            <div>
              <h3 className="font-semibold">{event.name}</h3>
              <p className="text-muted-foreground mt-1 text-sm leading-5">
                {event.description}
              </p>
            </div>
            {event.hasTicket && <Badge variant="secondary">Going</Badge>}
          </div>

          <div className="text-muted-foreground flex flex-col gap-2 text-sm">
            <div className="flex items-center gap-2">
              <CalendarDays className="size-4" />
              <span>{formatDate(event.eventTime)}</span>
            </div>
            <div className="flex items-center gap-2">
              <MapPin className="size-4" />
              <span>{event.venue}</span>
            </div>
            <div className="flex items-center gap-2">
              <Ticket className="size-4" />
              <span>
                {event.ticketsIssued} of {event.capacity} tickets reserved
              </span>
            </div>
          </div>

          <div className="mt-auto flex justify-end">
            <TicketAction event={event} />
          </div>
        </article>
      ))}
    </div>
  );
}
