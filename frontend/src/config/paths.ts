export const paths = {
  root: {
    path: '/',
    getHref: () => '/',
  },
  auth: {
    login: {
      path: '/auth/login',
      getHref: () => '/auth/login',
    },
    register: {
      path: '/auth/register',
      getHref: () => '/auth/register',
    },
  },
  clubs: {
    path: '/',
    getHref: () => '/',
  },
  club: {
    path: '/clubs/:id',
    getHref: (id: string) => `/clubs/${id}`,
  },
};
