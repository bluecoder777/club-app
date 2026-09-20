import { useAuth } from '@/features/auth/components/auth-provider';
import { NavUser } from './nav-user';

export const AppHeader = () => {
  const { user } = useAuth();
  return (
    <div className="flex items-center justify-between">
      <div className="flex items-center gap-2">
        <span className="text-lg font-semibold tracking-tight">Club House</span>
      </div>

      <NavUser
        user={{
          name: user?.name ?? '',
          email: user?.email ?? '',
        }}
      />
    </div>
  );
};
