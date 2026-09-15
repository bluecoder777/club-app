import { paths } from '@/config/paths';
import { Button } from '@/components/ui/button';
import { useNavigate } from 'react-router';

export const NotFound = () => {
  const navigate = useNavigate();

  const handleGoHome = () => {
    navigate(paths.root.getHref());
  };

  return (
    <div className="flex min-h-[calc(100vh-4rem)] items-center justify-center px-6">
      <div className="flex max-w-md flex-col items-center text-center">
        <span className="text-8xl font-black tracking-[-0.06em] text-foreground/10">
          404
        </span>

        <h1 className="mt-6 text-2xl font-semibold tracking-tight">
          This page doesn’t exist
        </h1>

        <p className="mt-3 text-sm leading-6 text-muted-foreground">
          The page may have been moved, deleted, or the URL might be incorrect.
        </p>

        <Button onClick={handleGoHome} className="mt-7">
          Back to home
        </Button>
      </div>
    </div>
  );
};
