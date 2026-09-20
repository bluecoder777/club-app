import { createBrowserRouter } from 'react-router';
import { RouterProvider } from 'react-router/dom';

import { MainErrorFallback } from '@/components/errors/main';
import { paths } from '@/config/paths';
import { AppLayout } from './layout';
import { RequireAuth } from '@/features/auth/components/require-auth';

const ProtectedAppLayout = () => {
  return (
    <RequireAuth>
      <AppLayout />
    </RequireAuth>
  );
};

const lazyLoad = <T extends Record<string, unknown>>(
  loader: () => Promise<T>,
  componentName: keyof T,
) =>
  loader().then((module) => ({
    Component: module[componentName] as React.ComponentType,
  }));

const createAppRouter = () =>
  createBrowserRouter([
    {
      path: paths.auth.login.path,
      lazy: () =>
        lazyLoad(() => import('../features/auth/routes/login'), 'Login'),
    },
    {
      path: paths.auth.register.path,
      lazy: () =>
        lazyLoad(() => import('../features/auth/routes/register'), 'Register'),
    },
    {
      path: paths.root.path,
      ErrorBoundary: MainErrorFallback,
      Component: ProtectedAppLayout,
      children: [
        {
          index: true,
          lazy: () =>
            lazyLoad(
              () => import('../features/clubs/routes/clubs'),
              'ClubsRoute',
            ),
        },
        {
          path: paths.club.path,
          lazy: () =>
            lazyLoad(() => import('../features/clubs/routes/club'), 'Club'),
        },
      ],
    },
    {
      path: '*',
      lazy: () =>
        lazyLoad(() => import('../components/errors/not-found'), 'NotFound'),
    },
  ]);

const router = createAppRouter();

export const AppRouter = () => {
  return <RouterProvider router={router} />;
};
