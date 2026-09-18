import { Button } from '@/components/ui/button';

export const MainErrorFallback = () => {
  return (
    <div
      className="flex min-h-screen items-center justify-center px-6"
      role="alert"
    >
      <div className="flex max-w-md flex-col items-center text-center">
        <span className="text-foreground/10 text-8xl font-black tracking-[-0.06em]">
          500
        </span>
        <h1 className="mt-6 text-2xl font-semibold tracking-tight">
          Something went wrong
        </h1>
        <p className="text-muted-foreground mt-3 text-sm leading-6">
          We couldn’t load this page. Please refresh and try again.
        </p>
        <Button
          className="mt-7"
          onClick={() => window.location.assign(window.location.origin)}
        >
          Refresh page
        </Button>
      </div>
    </div>
  );
};
