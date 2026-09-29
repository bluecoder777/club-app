import { Button } from '@/components/ui/button';
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog';
import { toast } from '@/components/ui/toast';
import { getErrorMessage } from '@/utils/get-error-message';
import { Plus } from 'lucide-react';
import { useState } from 'react';
import { useCreateEvent } from '../../api/use-create-event';
import { EventForm } from './event-form';

type CreateEventProps = {
  clubId: number;
};

export function CreateEvent({ clubId }: CreateEventProps) {
  const [open, setOpen] = useState(false);
  const createEvent = useCreateEvent({
    clubId,
    mutationConfig: {
      onSuccess: () => {
        toast.add({ title: 'Event created successfully!', type: 'success' });
        setOpen(false);
      },
      onError: (error) => {
        toast.add({
          title: getErrorMessage(error, 'Error creating event'),
          type: 'error',
        });
      },
    },
  });

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger render={<Button />}>
        <Plus /> New Event
      </DialogTrigger>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>Create Event</DialogTitle>
          <DialogDescription>
            Add an event for club members to attend.
          </DialogDescription>
        </DialogHeader>
        <EventForm
          onSubmit={(data) => createEvent.mutate({ ...data, clubId })}
        />
        <DialogFooter>
          <DialogClose
            render={
              <Button variant="outline" disabled={createEvent.isPending} />
            }
          >
            Cancel
          </DialogClose>
          <Button
            type="submit"
            form="event-form"
            disabled={createEvent.isPending}
          >
            {createEvent.isPending ? 'Creating...' : 'Create'}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
