import { useMemo, useState } from 'react';

import { useClubs } from '../../api/use-clubs';
import { ClubListEmpty } from './club-list-empty';
import { ClubListLoading } from './club-list-loading';
import { ClubCard } from './club-card';

type Section = 'discover' | 'myClubs' | 'joined';

const SECTIONS: { key: Section; label: string }[] = [
  { key: 'discover', label: 'Discover' },
  { key: 'myClubs', label: 'My Clubs' },
  { key: 'joined', label: 'Joined' },
];

export function ClubList() {
  const clubsQuery = useClubs();
  const [section, setSection] = useState<Section>('discover');

  const clubs = clubsQuery.data?.data ?? [];

  const grouped = useMemo(() => {
    return {
      discover: clubs.filter((c) => !c.isMember),
      myClubs: clubs.filter((c) => c.currentUserRole === 'ADMIN'),
      joined: clubs.filter((c) => c.isMember && c.currentUserRole !== 'ADMIN'),
    };
  }, [clubs]);

  if (clubsQuery.status === 'pending') {
    return <ClubListLoading />;
  }

  if (clubs.length === 0) {
    return <ClubListEmpty />;
  }

  const visibleClubs = grouped[section];

  return (
    <div className="flex items-start gap-6">
      <aside className="w-44 shrink-0 rounded-lg border bg-white p-2">
        <nav className="flex flex-col gap-0.5">
          {SECTIONS.map(({ key, label }) => (
            <button
              key={key}
              onClick={() => setSection(key)}
              className={`flex w-full items-center justify-between rounded-md px-3 py-2 text-left text-sm font-medium transition-colors ${
                section === key
                  ? 'bg-muted text-foreground'
                  : 'text-muted-foreground hover:bg-muted/50'
              }`}
            >
              {label}
              <span
                className={`rounded-full px-2 py-0.5 text-xs ${
                  section === key ? 'bg-background' : 'bg-muted'
                }`}
              >
                {grouped[key].length}
              </span>
            </button>
          ))}
        </nav>
      </aside>

      <div className="min-w-0 flex-1">
        {visibleClubs.length === 0 ? (
          <ClubListEmpty />
        ) : (
          <div className="grid grid-cols-[repeat(auto-fill,minmax(260px,320px))] gap-3">
            {visibleClubs.map((club) => (
              <ClubCard key={club.id} club={club} />
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
