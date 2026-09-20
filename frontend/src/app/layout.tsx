import { Outlet } from 'react-router';

import { AppHeader } from '@/components/app-header';

export function AppLayout() {
  return (
    <main className="bg-secondary min-h-screen">
      <header className="w-full border-b bg-white">
        <div className="mx-auto w-full max-w-7xl px-4">
          <AppHeader />
        </div>
      </header>

      <section className="w-full">
        <div className="mx-auto w-full max-w-7xl px-4 py-6">
          <Outlet />
        </div>
      </section>
    </main>
  );
}
