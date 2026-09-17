import { AuthLayout } from '../components/auth-layout';
import { RegisterForm } from '../components/register-form';
import { RedirectIfAuthenticated } from '../components/redirect-if-authenticated';

export const Register = () => {
  return (
    <RedirectIfAuthenticated>
      <AuthLayout mode="register">
        <RegisterForm />
      </AuthLayout>
    </RedirectIfAuthenticated>
  );
};
