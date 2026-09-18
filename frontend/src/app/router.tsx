import { createBrowserRouter } from 'react-router';
import { RouterProvider } from 'react-router/dom';

import { MainErrorFallback } from '@/components/errors/main';
import { paths } from '@/config/paths';
import { AppLayout } from './layout';

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
      Component: AppLayout,
      children: [
        {
          index: true,
          lazy: () => lazyLoad(() => import('../features/route/home'), 'Home'),
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
