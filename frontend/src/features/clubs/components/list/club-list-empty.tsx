import {
  Empty,
  EmptyDescription,
  EmptyHeader,
  EmptyMedia,
  EmptyTitle,
} from '@/components/ui/empty';
import { Users } from 'lucide-react';

export function ClubListEmpty() {
  return (
    <Empty className="min-h-72 rounded-xl bg-white">
      <EmptyHeader>
        <EmptyMedia variant="icon">
          <Users />
        </EmptyMedia>

        <EmptyTitle>No clubs found</EmptyTitle>

        <EmptyDescription>
          There are no clubs available yet. Create a club or check back later.
        </EmptyDescription>
      </EmptyHeader>
    </Empty>
  );
}
