import type { ReactNode } from 'react';
import { Navigate, useLocation } from 'react-router';

import { Spinner } from '@/components/ui/spinner';
import { paths } from '@/config/paths';

import { useAuth } from './auth-provider';

type RequireAuthProps = {
  children: ReactNode;
};

export const RequireAuth = ({ children }: RequireAuthProps) => {
  const { isAuthenticated, isInitializing } = useAuth();
  const location = useLocation();

  if (isInitializing) {
    return (
      <div className="flex h-screen items-center justify-center">
        <Spinner />
      </div>
    );
  }

  if (!isAuthenticated) {
    return (
      <Navigate
        to={paths.auth.login.getHref()}
        replace
        state={{ from: location }}
      />
    );
  }

  return children;
};
