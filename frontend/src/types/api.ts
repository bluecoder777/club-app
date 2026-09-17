import type { AxiosError } from 'axios';

export interface ApiErrorResponse {
  status: 'ERROR';
  error: {
    message: string;
    code: number;
  };
}

export type ApiError = AxiosError<ApiErrorResponse>;
