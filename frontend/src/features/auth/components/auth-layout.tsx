import type { ReactNode } from 'react';

import { paths } from '@/config/paths';

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

  return (
    <div className="flex min-h-screen w-full items-center justify-center px-6">
      <div className="w-full max-w-90">
        <div className="mb-9 text-left">
          <h1 className="mb-1.5 text-[22px] font-semibold tracking-tight">
            {title}
          </h1>
          <p className="text-sm leading-relaxed text-zinc-500">{description}</p>
        </div>
        {children}
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
