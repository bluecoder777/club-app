import { AxiosError } from 'axios';

export const getErrorMessage = (
  error: unknown,
  defaultMessage = 'An error occurred. Please try again.',
): string => {
  if (error instanceof AxiosError) {
    return error.response?.data?.error?.message || defaultMessage;
  }

  return defaultMessage;
};
