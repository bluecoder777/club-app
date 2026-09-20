import { LogOut } from 'lucide-react';

import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { Button } from '@/components/ui/button';
import { Separator } from '@/components/ui/separator';
import { useAuth } from '@/features/auth/components/auth-provider';

export function NavUser({ user }: { user: { name: string; email: string } }) {
  const { logout } = useAuth();

  const handleLogout = async () => {
    await logout();
  };

  return (
    <div className="flex items-center gap-4 px-4 py-3">
      <Avatar className="h-9 w-9 rounded-lg">
        <AvatarFallback className="rounded-lg">
          {user.name.charAt(0).toUpperCase()}
        </AvatarFallback>
      </Avatar>

      <div className="min-w-0 flex-1">
        <p className="truncate text-sm font-medium">{user.name}</p>
        <p className="text-muted-foreground truncate text-xs">{user.email}</p>
      </div>

      <Separator orientation="vertical" className="h-6" />

      <Button
        variant="ghost"
        size="icon"
        onClick={handleLogout}
        title="Log out"
      >
        <LogOut className="size-4" />
      </Button>
    </div>
  );
}
