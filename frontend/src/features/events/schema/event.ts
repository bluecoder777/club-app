import { z } from 'zod';

export const eventSchema = z.object({
  name: z
    .string()
    .trim()
    .min(1, 'Name is required')
    .max(100, 'Name must be 100 characters or less'),
  description: z.string().trim().min(1, 'Description is required'),
  venue: z
    .string()
    .trim()
    .min(1, 'Venue is required')
    .max(200, 'Venue must be 200 characters or less'),
  eventTime: z
    .string()
    .min(1, 'Event time is required')
    .refine((value) => new Date(value).getTime() > Date.now(), {
      message: 'Event time must be in the future',
    }),
  capacity: z.coerce
    .number<number>()
    .int('Capacity must be a whole number')
    .min(1, 'Capacity must be at least 1'),
});

export type EventFormData = z.infer<typeof eventSchema>;
