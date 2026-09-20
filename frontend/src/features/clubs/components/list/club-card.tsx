import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader } from '@/components/ui/card';
import { useNavigate } from 'react-router';

import { paths } from '@/config/paths';
import { cn } from '@/lib/utils';

import type { Club } from '../../types';
import JoinClub from '../actions/join-club';

type ClubCardProps = {
  club: Club;
};

function getInitials(name: string) {
  return name
    .trim()
    .split(/\s+/)
    .slice(0, 2)
    .map((w) => w[0]?.toUpperCase())
    .join('');
}

export function ClubCard({ club }: ClubCardProps) {
  const navigate = useNavigate();
  const isAdmin = club.currentUserRole === 'ADMIN';

  const handleOpen = () => {
    if (club.isMember) {
      navigate(paths.club.getHref(club.id.toString()));
    }
  };

  return (
    <Card
      role={club.isMember ? 'button' : undefined}
      tabIndex={club.isMember ? 0 : undefined}
      onClick={handleOpen}
      onKeyDown={(e) => {
        if (club.isMember && (e.key === 'Enter' || e.key === ' ')) {
          e.preventDefault();
          handleOpen();
        }
      }}
      className={cn(
        'border-0! shadow-none! ring-0!',
        'flex h-full flex-col',
        club.isMember && 'hover:bg-muted/40 cursor-pointer transition-colors',
      )}
    >
      <CardHeader className="flex flex-row items-start gap-3 space-y-0">
        <div className="bg-muted flex h-9 w-9 shrink-0 items-center justify-center rounded-lg text-sm font-semibold">
          {getInitials(club.name)}
        </div>

        <div className="min-w-0 flex-1">
          <h3 className="truncate leading-tight font-semibold">{club.name}</h3>
          <span className="text-muted-foreground text-xs">
            {club.memberCount} {club.memberCount === 1 ? 'member' : 'members'}
          </span>
        </div>

        {isAdmin && (
          <Badge variant="outline" className="shrink-0 text-[10px] uppercase">
            Admin
          </Badge>
        )}
      </CardHeader>

      <CardContent className="flex flex-1 flex-col space-y-4">
        <p className="text-muted-foreground line-clamp-2 text-sm">
          {club.description}
        </p>

        <div className="mt-auto">
          {isAdmin ? (
            <Button variant="outline" className="w-full" onClick={handleOpen}>
              Manage
            </Button>
          ) : club.isMember ? (
            <Button variant="outline" className="w-full" onClick={handleOpen}>
              View Club
            </Button>
          ) : (
            <JoinClub clubId={club.id} clubName={club.name} />
          )}
        </div>
      </CardContent>
    </Card>
  );
}
