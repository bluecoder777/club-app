import { Badge } from '@/components/ui/badge';
import { Skeleton } from '@/components/ui/skeleton';
import { paths } from '@/config/paths';
import { permissions } from '@/utils/permissions';
import { Navigate } from 'react-router';
import { useClub } from '../../api/use-club';
import { EditClub } from '../actions/edit-club';
import LeaveClub from '../actions/leave-club';
import { cn } from 'cn';

type ClubDetailsHeaderProps = {
  clubId: number;
};

const ClubDetailsHeader = ({ clubId }: ClubDetailsHeaderProps) => {
  const clubQuery = useClub({ clubId });

  if (clubQuery.isLoading) {
    return <ClubDetailsHeaderSkeleton />;
  }

  if (clubQuery.isError || !clubQuery.data?.data) {
    return (
      <div className="text-muted-foreground rounded-lg bg-white p-4 text-sm">
        Unable to load club details.
      </div>
    );
  }

  const club = clubQuery.data.data;

  if (!club.isMember) {
    return <Navigate to={paths.root.getHref()} />;
  }

  const canEdit =
    club.currentUserRole && permissions.editClub(club.currentUserRole);

  return (
    <div className="rounded-lg bg-white">
      <div className="flex min-h-24 items-center justify-between gap-4 p-4">
        <div className="min-w-0 flex-1">
          <div className="flex flex-wrap items-center gap-2">
            <h1 className="truncate text-xl font-semibold tracking-tight md:text-2xl">
              {club.name}
            </h1>

            <Badge variant="secondary">
              {club.memberCount} {club.memberCount === 1 ? 'member' : 'members'}
            </Badge>

            <Badge
              variant="secondary"
              className={cn(
                club.currentUserRole === 'ADMIN' &&
                  'bg-primary text-primary-foreground',
              )}
            >
              {club.currentUserRole}
            </Badge>
          </div>

          {club.description && (
            <p className="text-muted-foreground mt-1.5 line-clamp-2 max-w-2xl text-sm">
              {club.description}
            </p>
          )}
        </div>

        <div className="flex shrink-0 items-center gap-2">
          <LeaveClub clubId={club.id} clubName={club.name} />
          {canEdit && <EditClub club={club} />}
        </div>
      </div>
    </div>
  );
};

function ClubDetailsHeaderSkeleton() {
  return (
    <div className="rounded-lg bg-white">
      <div className="flex min-h-24 items-center justify-between gap-4 p-4">
        <div className="flex-1 space-y-2">
          <div className="flex items-center gap-2">
            <Skeleton className="h-8 w-48" />
            <Skeleton className="h-6 w-20 rounded-full" />
          </div>

          <Skeleton className="h-4 w-80 max-w-full" />
        </div>

        <div className="flex shrink-0 gap-2">
          <Skeleton className="h-8 w-20" />
          <Skeleton className="h-8 w-24" />
          <Skeleton className="h-8 w-20" />
        </div>
      </div>
    </div>
  );
}

export default ClubDetailsHeader;
