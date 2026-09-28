import type { AxiosError } from 'axios';

export interface ApiErrorResponse {
  status: 'ERROR';
  error: {
    message: string;
    code: number;
    key?: string;
  };
}

export type ApiError = AxiosError<ApiErrorResponse>;
