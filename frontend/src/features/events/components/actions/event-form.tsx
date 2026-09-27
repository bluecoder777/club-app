import { InputFormField } from '@/components/form/input-form-field';
import { TextAreaFormField } from '@/components/form/text-area-form-field';
import { FieldGroup } from '@/components/ui/field';
import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import { eventSchema, type EventFormData } from '../../schema/event';

type EventFormProps = {
  onSubmit: (data: EventFormData) => void;
};

export function EventForm({ onSubmit }: EventFormProps) {
  const form = useForm<EventFormData>({
    resolver: zodResolver(eventSchema),
    defaultValues: {
      name: '',
      description: '',
      venue: '',
      eventTime: '',
      capacity: 1,
    },
  });

  return (
    <form id="event-form" onSubmit={form.handleSubmit(onSubmit)}>
      <FieldGroup>
        <InputFormField control={form.control} name="name" label="Name" />
        <TextAreaFormField
          control={form.control}
          name="description"
          label="Description"
        />
        <InputFormField control={form.control} name="venue" label="Venue" />
        <InputFormField
          control={form.control}
          name="eventTime"
          label="Date and time"
          type="datetime-local"
        />
        <InputFormField
          control={form.control}
          name="capacity"
          label="Capacity"
          type="number"
          min={1}
          step={1}
        />
      </FieldGroup>
    </form>
  );
}
