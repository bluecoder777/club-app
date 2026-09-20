import * as React from 'react';
import { useCookies } from 'react-cookie';

import { api, configureApiAuth, refreshAccessToken } from '@/lib/api-client';
import { AUTH_ENDPOINTS } from '../config/endpoints';

import type { AuthData, AuthResponse, AuthUser } from '../types';
import { useQueryClient } from '@tanstack/react-query';

const REFRESH_TOKEN_COOKIE = 'refreshToken';
const USER_STORAGE_KEY = 'user';

type AuthContextValue = {
  accessToken: string | null;
  user: AuthUser | null;
  isAuthenticated: boolean;
  isInitializing: boolean;
  login: (auth: AuthResponse | AuthData) => void;
  logout: () => Promise<void>;
};

const AuthContext = React.createContext<AuthContextValue | undefined>(
  undefined,
);

export const useAuth = () => {
  const context = React.useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

type AuthProviderProps = {
  children: React.ReactNode;
};

export const AuthProvider = ({ children }: AuthProviderProps) => {
  const [accessToken, setAccessToken] = React.useState<string | null>(null);
  const [user, setUser] = React.useState<AuthUser | null>(null);
  const [isInitializing, setIsInitializing] = React.useState(true);

  const [cookies, setCookie, removeCookie] = useCookies([REFRESH_TOKEN_COOKIE]);

  const queryClient = useQueryClient();

  const tokenRef = React.useRef<string | null>(null);
  const refreshTokenRef = React.useRef<string | null>(
    cookies[REFRESH_TOKEN_COOKIE] ?? null,
  );

  const setToken = React.useCallback((token: string | null) => {
    tokenRef.current = token;
    setAccessToken(token);
  }, []);

  const setRefreshToken = React.useCallback(
    (token: string | null) => {
      refreshTokenRef.current = token;
      if (token) {
        setCookie(REFRESH_TOKEN_COOKIE, token, {
          path: '/',
          maxAge: 30 * 24 * 60 * 60,
          sameSite: 'strict',
        });
      } else {
        removeCookie(REFRESH_TOKEN_COOKIE, { path: '/' });
      }
    },
    [setCookie, removeCookie],
  );

  const persistUser = React.useCallback((u: AuthUser | null) => {
    if (u) {
      localStorage.setItem(USER_STORAGE_KEY, JSON.stringify(u));
    } else {
      localStorage.removeItem(USER_STORAGE_KEY);
    }
    setUser(u);
  }, []);

  const clearSession = React.useCallback(() => {
    setToken(null);
    persistUser(null);
    setRefreshToken(null);
  }, [setToken, persistUser, setRefreshToken]);

  const logout = React.useCallback(async () => {
    const currentRefreshToken = refreshTokenRef.current;
    try {
      await api.post(
        AUTH_ENDPOINTS.LOGOUT,
        currentRefreshToken ? { refreshToken: currentRefreshToken } : undefined,
        { skipAuth: true },
      );
    } finally {
      queryClient.clear();
      clearSession();
    }
  }, [clearSession]);

  React.useEffect(() => {
    refreshTokenRef.current = cookies[REFRESH_TOKEN_COOKIE] ?? null;

    const stored = localStorage.getItem(USER_STORAGE_KEY);
    if (stored) {
      try {
        setUser(JSON.parse(stored) as AuthUser);
      } catch {
        localStorage.removeItem(USER_STORAGE_KEY);
      }
    }

    configureApiAuth({
      getAccessToken: () => tokenRef.current,
      getRefreshToken: () => refreshTokenRef.current,
      onAccessTokenRefreshed: setToken,
      onRefreshTokenRotated: (newRefreshToken) =>
        setRefreshToken(newRefreshToken),
      onUnauthorized: clearSession,
    });

    void refreshAccessToken()
      .then((accessToken) => {
        setToken(accessToken);
      })
      .catch(() => {
        persistUser(null);
      })
      .finally(() => {
        setIsInitializing(false);
      });

    return () => configureApiAuth(null);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const login = React.useCallback(
    (auth: AuthResponse | AuthData) => {
      const data = 'data' in auth ? auth.data : auth;
      setToken(data.accessToken);
      setRefreshToken(data.refreshToken);
      persistUser(data.user);
      queryClient.clear();
    },
    [setToken, setRefreshToken, persistUser],
  );

  const value = React.useMemo<AuthContextValue>(
    () => ({
      accessToken,
      user,
      isAuthenticated: Boolean(accessToken && user),
      isInitializing,
      login,
      logout,
    }),
    [accessToken, isInitializing, login, logout, user],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
