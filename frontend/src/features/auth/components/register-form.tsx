import { InputFormField } from '@/components/form/input-form-field';
import { PasswordFormField } from '@/components/form/password-form-field';
import { Button } from '@/components/ui/button';
import { FieldGroup } from '@/components/ui/field';
import { paths } from '@/config/paths';
import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router';

import { useRegister } from '../api/use-register';
import { registrationSchema, type RegistrationRequest } from '../schema/auth';
import { getErrorMessage } from '@/utils/get-error-message';
import { toast } from '@/components/ui/toast';

export const RegisterForm = () => {
  const navigate = useNavigate();
  const { mutate, isPending } = useRegister({
    mutationConfig: {
      onSuccess: () => {
        toast.add({
          title: 'Account created successfully!',
          type: 'success',
        });
        navigate(paths.auth.login.getHref(), { replace: true });
      },
      onError: (data) => {
        toast.add({
          title: getErrorMessage(data),
          type: 'error',
        });
      },
    },
  });
  const form = useForm<RegistrationRequest>({
    resolver: zodResolver(registrationSchema),
    defaultValues: {
      email: '',
      password: '',
      name: '',
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
          name="name"
          label="Name"
          type="text"
          placeholder="Enter your name"
          className="h-11"
        />
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
          {isPending ? 'Creating account…' : 'Register'}
        </Button>
      </FieldGroup>
    </form>
  );
};
