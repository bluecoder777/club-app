import { AuthLayout } from '../components/auth-layout';
import { LoginForm } from '../components/login-form';
import { RedirectIfAuthenticated } from '../components/redirect-if-authenticated';

export const Login = () => {
  return (
    <RedirectIfAuthenticated>
      <AuthLayout mode="login">
        <LoginForm />
      </AuthLayout>
    </RedirectIfAuthenticated>
  );
};
