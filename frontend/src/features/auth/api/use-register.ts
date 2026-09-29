import type { MutationConfig } from '@/lib/react-query';
import type { RegistrationRequest } from '../schema/auth';
import type { AuthResponse } from '../types';
import { api } from '@/lib/api-client';
import { AUTH_ENDPOINTS } from '../config/endpoints';
import { useMutation } from '@tanstack/react-query';

function registerUser(data: RegistrationRequest): Promise<AuthResponse> {
  return api.post(AUTH_ENDPOINTS.REGISTER, data, { skipAuth: true });
}

type UseRegisterOptions = {
  mutationConfig?: MutationConfig<typeof registerUser>;
};

export const useRegister = ({ mutationConfig }: UseRegisterOptions = {}) => {
  return useMutation({
    mutationFn: registerUser,
    ...mutationConfig,
  });
};
