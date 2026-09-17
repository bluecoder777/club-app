import Axios, { type AxiosError, type InternalAxiosRequestConfig } from 'axios';

declare module 'axios' {
  interface AxiosRequestConfig {
    _retry?: boolean;
    skipAuth?: boolean;
  }
}

import { env } from '@/config/env';

type AuthCallbacks = {
  getAccessToken: () => string | null;
  getRefreshToken: () => string | null;
  onAccessTokenRefreshed: (accessToken: string) => void;
  onRefreshTokenRotated: (refreshToken: string) => void;
  onUnauthorized: () => void;
};

type RetriableRequestConfig = InternalAxiosRequestConfig & {
  _retry?: boolean;
  skipAuth?: boolean;
};

type RefreshResponse = {
  data: {
    accessToken: string;
    refreshToken: string;
  };
};

let authCallbacks: AuthCallbacks | null = null;
let refreshPromise: Promise<string> | null = null;

export const configureApiAuth = (callbacks: AuthCallbacks | null) => {
  authCallbacks = callbacks;
};

export const api = Axios.create({
  baseURL: env.API_URL,
  withCredentials: true,
});

const refreshClient = Axios.create({
  baseURL: env.API_URL,
  withCredentials: true,
});

function authRequestInterceptor(config: RetriableRequestConfig) {
  config.headers.Accept = 'application/json';

  const accessToken = authCallbacks?.getAccessToken();
  if (accessToken && !config.skipAuth) {
    config.headers.Authorization = `Bearer ${accessToken}`;
  }

  return config;
}

export const refreshAccessToken = (): Promise<string> => {
  if (!refreshPromise) {
    const refreshToken = authCallbacks?.getRefreshToken() ?? null;
    if (!refreshToken) {
      return Promise.reject(new Error('No refresh token available'));
    }

    refreshPromise = refreshClient
      .post<RefreshResponse>('auth/refresh', { refreshToken })
      .then((response) => {
        const { accessToken, refreshToken: newRefreshToken } =
          response.data.data;
        if (!accessToken) {
          throw new Error('Refresh response did not include an access token');
        }

        authCallbacks?.onAccessTokenRefreshed(accessToken);
        if (newRefreshToken) {
          authCallbacks?.onRefreshTokenRotated(newRefreshToken);
        }
        return accessToken;
      })
      .finally(() => {
        refreshPromise = null;
      });
  }

  return refreshPromise;
};

api.interceptors.request.use(authRequestInterceptor);
api.interceptors.response.use(
  (response) => response.data,
  async (error: AxiosError) => {
    const request = error.config as RetriableRequestConfig | undefined;

    if (
      error.response?.status !== 401 ||
      !request ||
      request._retry ||
      request.skipAuth
    ) {
      return Promise.reject(error);
    }

    request._retry = true;

    try {
      const accessToken = await refreshAccessToken();
      request.headers.Authorization = `Bearer ${accessToken}`;
      return api.request(request);
    } catch (refreshError) {
      authCallbacks?.onUnauthorized();
      return Promise.reject(refreshError);
    }
  },
);
