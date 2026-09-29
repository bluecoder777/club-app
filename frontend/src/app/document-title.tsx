import { useEffect } from 'react';
import { matchPath, useLocation } from 'react-router';

import { APP_NAME } from '@/config/app';
import { paths } from '@/config/paths';

const pageTitles = [
  { path: paths.auth.login.path, title: 'Sign in' },
  { path: paths.auth.register.path, title: 'Create account' },
  { path: paths.club.path, title: 'Club' },
  { path: paths.root.path, title: 'Clubs' },
];

export const DocumentTitle = () => {
  const { pathname } = useLocation();

  useEffect(() => {
    const page = pageTitles.find(({ path }) =>
      matchPath({ path, end: true }, pathname),
    );

    document.title = page
      ? `${page.title} | ${APP_NAME}`
      : `Page not found | ${APP_NAME}`;
  }, [pathname]);

  return null;
};
