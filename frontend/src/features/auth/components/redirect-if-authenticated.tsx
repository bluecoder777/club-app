import type { ReactNode } from 'react';
import { Navigate } from 'react-router';

import { Spinner } from '@/components/ui/spinner';
import { paths } from '@/config/paths';

import { useAuth } from './auth-provider';

type RedirectIfAuthenticatedProps = {
  children: ReactNode;
};

export const RedirectIfAuthenticated = ({
  children,
}: RedirectIfAuthenticatedProps) => {
  const { isAuthenticated, isInitializing } = useAuth();

  if (isInitializing) {
    return (
      <div className="flex h-screen items-center justify-center">
        <Spinner />
      </div>
    );
  }

  if (isAuthenticated) {
    return <Navigate to={paths.root.getHref()} replace />;
  }

  return children;
};
