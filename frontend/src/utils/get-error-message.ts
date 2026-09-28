import axios from 'axios';
import type { ApiErrorResponse } from '@/types/api';

export const getErrorMessage = (
  error: unknown,
  defaultMessage = 'An error occurred. Please try again.',
): string => {
  if (axios.isAxiosError<ApiErrorResponse>(error)) {
    return error.response?.data?.error?.message || defaultMessage;
  }

  return defaultMessage;
};

export const getErrorKey = (error: unknown): string | undefined => {
  if (axios.isAxiosError<ApiErrorResponse>(error)) {
    return error.response?.data?.error?.key;
  }

  return undefined;
};
