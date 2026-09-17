import * as React from 'react';
import { LogIn } from 'lucide-react';

import { NavUser } from '@/components/nav-user';
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
} from '@/components/ui/sidebar';
import { paths } from '@/config/paths';
import { useAuth } from '@/features/auth/components/auth-provider';
import { Club } from 'lucide-react';

export function AppSidebar({ ...props }: React.ComponentProps<typeof Sidebar>) {
  const { user, isAuthenticated } = useAuth();

  return (
    <Sidebar variant="inset" collapsible="offcanvas" {...props}>
      <SidebarHeader>
        <SidebarMenu>
          <SidebarMenuItem>
            <SidebarMenuButton
              className="data-[slot=sidebar-menu-button]:p-1.5!"
              render={
                <a
                  href={paths.root.getHref()}
                  className="flex items-center gap-2"
                >
                  <Club className="size-5!" />
                  <span className="text-base font-semibold">Clubhouse</span>
                </a>
              }
            />
          </SidebarMenuItem>
        </SidebarMenu>
      </SidebarHeader>
      <SidebarContent />

      <SidebarFooter>
        {isAuthenticated && user ? (
          <NavUser
            user={{
              name: user.name,
              email: user.email,
            }}
          />
        ) : (
          <SidebarMenu>
            <SidebarMenuItem>
              <SidebarMenuButton
                render={
                  <a
                    href={paths.auth.login.getHref()}
                    className="text-sidebar-foreground bg-sidebar-accent hover:text-sidebar-accent-foreground flex items-center justify-center border border-transparent px-4 py-2 text-sm font-medium transition-colors"
                  >
                    <LogIn />
                    <span>Login</span>
                  </a>
                }
              ></SidebarMenuButton>
            </SidebarMenuItem>
          </SidebarMenu>
        )}
      </SidebarFooter>
    </Sidebar>
  );
}
