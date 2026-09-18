import type { ReactNode } from 'react';

import { Button } from '@/components/ui/button';
import { paths } from '@/config/paths';
import { useNavigate } from 'react-router';

type AuthMode = 'login' | 'register';

type AuthLayoutProps = {
  children: ReactNode;
  mode: AuthMode;
};

const authConfig = {
  login: {
    title: 'Log in to your account',
    description: 'Welcome back. Enter your details to continue.',
    prompt: "Don't have an account?",
    linkLabel: 'Register',
    linkHref: paths.auth.register.getHref(),
  },
  register: {
    title: 'Register an account',
    description: 'Create an account to get started.',
    prompt: 'Already have an account?',
    linkLabel: 'Log in',
    linkHref: paths.auth.login.getHref(),
  },
} satisfies Record<
  AuthMode,
  {
    title: string;
    description: string;
    prompt: string;
    linkLabel: string;
    linkHref: string;
  }
>;

export const AuthLayout = ({ children, mode }: AuthLayoutProps) => {
  const { title, description, prompt, linkLabel, linkHref } = authConfig[mode];
  const navigate = useNavigate();

  const handleSkip = () => {
    navigate(paths.root.getHref(), { replace: true });
  };

  return (
    <div className="flex min-h-screen w-full items-center justify-center bg-zinc-950 px-6">
      <div className="w-full max-w-90">
        <div className="mb-9 text-left">
          <h1 className="mb-1.5 text-[22px] font-semibold tracking-tight text-zinc-50">
            {title}
          </h1>
          <p className="text-sm leading-relaxed text-zinc-500">{description}</p>
        </div>
        {children}
        <div className="my-5 flex items-center gap-3">
          <div className="h-px flex-1 bg-zinc-800" />
          <span className="text-xs text-zinc-600">or</span>
          <div className="h-px flex-1 bg-zinc-800" />
        </div>

        <Button
          type="button"
          variant="outline"
          onClick={handleSkip}
          className="h-11 w-full rounded-[10px] border-zinc-800 bg-transparent text-sm font-medium text-zinc-500 hover:border-zinc-600 hover:bg-transparent hover:text-zinc-100"
        >
          Skip for now
        </Button>

        <p className="mt-8 text-sm text-gray-500">
          {prompt}{' '}
          <a href={linkHref} className="text-primary hover:underline">
            {linkLabel}
          </a>
        </p>
      </div>
    </div>
  );
};
