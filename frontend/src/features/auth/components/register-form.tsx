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
          className="h-11 rounded-[10px] border-zinc-800 bg-zinc-900 pr-10 text-zinc-100 placeholder:text-zinc-600 focus-visible:ring-1 focus-visible:ring-zinc-600 focus-visible:ring-offset-0"
        />
        <InputFormField
          control={form.control}
          name="email"
          label="Email"
          type="email"
          placeholder="Enter your email"
          className="h-11 rounded-[10px] border-zinc-800 bg-zinc-900 pr-10 text-zinc-100 placeholder:text-zinc-600 focus-visible:ring-1 focus-visible:ring-zinc-600 focus-visible:ring-offset-0"
        />
        <PasswordFormField
          control={form.control}
          name="password"
          label="Password"
          placeholder="Enter your password"
          className="h-11 rounded-[10px] border-zinc-800 bg-zinc-900 pr-10 text-zinc-100 placeholder:text-zinc-600 focus-visible:ring-1 focus-visible:ring-zinc-600 focus-visible:ring-offset-0"
        />
        <Button
          type="submit"
          className="h-11 w-full rounded-[10px] bg-zinc-100 text-sm font-semibold text-zinc-950 hover:bg-zinc-100/90"
          disabled={isPending}
        >
          {isPending ? 'Creating account…' : 'Register'}
        </Button>
      </FieldGroup>
    </form>
  );
};
