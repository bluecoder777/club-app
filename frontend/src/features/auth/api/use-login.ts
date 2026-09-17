import { api } from '@/lib/api-client';
import type { MutationConfig } from '@/lib/react-query';
import { useMutation } from '@tanstack/react-query';
import { AUTH_ENDPOINTS } from '../config/endpoints';
import type { LoginRequest } from '../schema/auth';
import type { AuthResponse } from '../types';

function login(data: LoginRequest): Promise<AuthResponse> {
  return api.post(AUTH_ENDPOINTS.LOGIN, data, { skipAuth: true });
}

type UseLoginOptions = {
  mutationConfig?: MutationConfig<typeof login>;
};

export const useLogin = ({ mutationConfig }: UseLoginOptions = {}) => {
  return useMutation({
    mutationFn: login,
    ...mutationConfig,
  });
};
