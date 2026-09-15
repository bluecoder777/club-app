import { createBrowserRouter, type RouteObject } from 'react-router';
import { RouterProvider } from 'react-router/dom';

import { paths } from '@/config/paths';
import { MainErrorFallback } from '@/components/errors/main';

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
      path: paths.root.path,
      ErrorBoundary: MainErrorFallback,
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
