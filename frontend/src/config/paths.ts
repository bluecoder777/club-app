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
};
