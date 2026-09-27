import { useClub } from '@/features/clubs/api/use-club';
import { permissions } from '@/utils/permissions';
import { useEvents } from '../api/use-events';
import { CreateEvent } from './actions/create-event';
import { EventList, EventListSkeleton } from './event-list';

type EventsProps = {
  clubId: number;
};

export function Events({ clubId }: EventsProps) {
  const eventsQuery = useEvents({ clubId });
  const clubQuery = useClub({ clubId });

  if (eventsQuery.isLoading || clubQuery.isLoading) {
    return <EventListSkeleton />;
  }

  const role = clubQuery.data?.data.currentUserRole;
  const canCreateEvent = !!role && permissions.createEvent(role);

  return (
    <div className="flex flex-col gap-4 rounded-2xl bg-white p-4">
      <div className="flex items-center justify-between">
        <h2 className="text-lg font-semibold">Club Events</h2>
        {canCreateEvent && <CreateEvent clubId={clubId} />}
      </div>
      <EventList events={eventsQuery.data?.data ?? []} />
    </div>
  );
}
