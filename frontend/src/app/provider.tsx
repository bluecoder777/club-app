import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import * as React from 'react';
import { ErrorBoundary } from 'react-error-boundary';
import { CookiesProvider } from 'react-cookie';
import { Toaster } from '@/components/ui/toast';

import { MainErrorFallback } from '@/components/errors/main';
import { Spinner } from '@/components/ui/spinner';
import { AuthProvider } from '@/features/auth/components/auth-provider';
import { queryConfig } from '@/lib/react-query';

type AppProviderProps = {
  children: React.ReactNode;
};

export const AppProvider = ({ children }: AppProviderProps) => {
  const [queryClient] = React.useState(
    () =>
      new QueryClient({
        defaultOptions: queryConfig,
      }),
  );

  return (
    <React.Suspense
      fallback={
        <div className="flex h-screen w-screen items-center justify-center">
          <Spinner />
        </div>
      }
    >
      <ErrorBoundary FallbackComponent={MainErrorFallback}>
        <CookiesProvider>
          <QueryClientProvider client={queryClient}>
            <AuthProvider>{children}</AuthProvider>
            <Toaster />
          </QueryClientProvider>
        </CookiesProvider>
      </ErrorBoundary>
    </React.Suspense>
  );
};
