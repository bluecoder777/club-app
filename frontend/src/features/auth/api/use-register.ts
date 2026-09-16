import type { MutationConfig } from '@/lib/react-query';
import type { RegistrationRequest } from '../schema/auth';
import { api } from '@/lib/api-client';
import { AUTH_ENDPOINTS } from '../config/endpoints';
import { useMutation } from '@tanstack/react-query';
type RegisterResponse = {
  status: 'SUCCESS';
  data: string;
};

function registerUser(data: RegistrationRequest): Promise<RegisterResponse> {
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
