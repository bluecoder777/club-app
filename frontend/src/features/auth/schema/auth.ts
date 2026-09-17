import { z } from 'zod';

export const registrationSchema = z.object({
  name: z
    .string()
    .trim()
    .min(2, { error: 'Name must be at least 2 characters' })
    .max(100, { error: 'Name is too long' }),

  email: z.email({
    error: 'Enter a valid email address',
  }),

  password: z
    .string()
    .min(8, { error: 'Password must be at least 8 characters' })
    .max(72, { error: 'Password is too long' }),
});

export const loginSchema = z.object({
  email: z.email({
    error: 'Enter a valid email address',
  }),

  password: z.string().min(1, {
    error: 'Password is required',
  }),
});

export type RegistrationRequest = z.infer<typeof registrationSchema>;
export type LoginRequest = z.infer<typeof loginSchema>;
