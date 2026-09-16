export type AuthUser = {
  id: number;
  name: string;
  email: string;
};

export type AuthData = {
  accessToken: string;
  refreshToken: string;
  user: AuthUser;
};

export type AuthResponse = {
  status: 'SUCCESS';
  data: AuthData;
};
