import { InputFormField } from '@/components/form/input-form-field';
import { PasswordFormField } from '@/components/form/password-form-field';
import { Button } from '@/components/ui/button';
import { FieldGroup } from '@/components/ui/field';
import { toast } from '@/components/ui/toast';
import { paths } from '@/config/paths';
import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router';

import { useLogin } from '../api/use-login';
import { loginSchema, type LoginRequest } from '../schema/auth';
import { useAuth } from './auth-provider';
import { getErrorMessage } from '@/utils/get-error-message';

export const LoginForm = () => {
  const navigate = useNavigate();
  const { login } = useAuth();
  const { mutate, isPending } = useLogin({
    mutationConfig: {
      onSuccess: (response) => {
        login(response);
        toast.add({
          title: 'Logged in successfully!',
          type: 'success',
        });
        navigate(paths.root.getHref(), { replace: true });
      },
      onError: (data) => {
        toast.add({
          title: getErrorMessage(data),
          type: 'error',
        });
      },
    },
  });
  const form = useForm<LoginRequest>({
    resolver: zodResolver(loginSchema),
    defaultValues: {
      email: '',
      password: '',
    },
  });

  return (
    <form
      onSubmit={form.handleSubmit((data) => mutate(data))}
      className="w-full sm:max-w-md"
    >
      <FieldGroup>
        <InputFormField
          control={form.control}
          name="email"
          label="Email"
          type="email"
          placeholder="Enter your email"
          className="h-11"
        />
        <PasswordFormField
          control={form.control}
          name="password"
          label="Password"
          placeholder="Enter your password"
          className="h-11"
        />
        <Button type="submit" className="h-11 w-full" disabled={isPending}>
          {isPending ? 'Logging in…' : 'Login'}
        </Button>
      </FieldGroup>
    </form>
  );
};
