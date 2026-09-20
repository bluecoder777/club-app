import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import { Skeleton } from '@/components/ui/skeleton';
import {
  Empty,
  EmptyDescription,
  EmptyHeader,
  EmptyTitle,
} from '@/components/ui/empty';
import { Users } from 'lucide-react';
import type { ClubMember } from '../types';
import { formatDate } from '@/utils/forma-date';
import RemoveClubMember from './remove-club-member';
import MemberRole from './member-role';

type MemberTableProps = {
  members?: ClubMember[];
  isLoading?: boolean;
  clubId: number;
  canManageMembers: boolean;
};

function MemberTableSkeleton() {
  return (
    <div className="rounded-md border">
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead>Member</TableHead>
            <TableHead>Email</TableHead>
            <TableHead>Joined</TableHead>
            <TableHead>Role</TableHead>
            <TableHead></TableHead>
          </TableRow>
        </TableHeader>

        <TableBody>
          {Array.from({ length: 5 }).map((_, index) => (
            <TableRow key={index}>
              <TableCell>
                <Skeleton className="h-5 w-36" />
              </TableCell>
              <TableCell>
                <Skeleton className="h-5 w-52" />
              </TableCell>
              <TableCell>
                <Skeleton className="h-5 w-24" />
              </TableCell>
              <TableCell>
                <Skeleton className="h-5 w-20" />
              </TableCell>
              <TableCell>
                <Skeleton className="h-5 w-20" />
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </div>
  );
}

export function MemberTable({
  members = [],
  isLoading = false,
  clubId,
  canManageMembers,
}: MemberTableProps) {
  if (isLoading) {
    return <MemberTableSkeleton />;
  }

  if (!members.length) {
    return (
      <Empty className="min-h-75 rounded-md border">
        <EmptyHeader>
          <Users className="text-muted-foreground size-10" />
          <EmptyTitle>No members yet</EmptyTitle>
          <EmptyDescription>
            There are no members in this club yet.
          </EmptyDescription>
        </EmptyHeader>
      </Empty>
    );
  }

  return (
    <div className="rounded-md border">
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead>Member</TableHead>
            <TableHead>Email</TableHead>
            <TableHead>Joined</TableHead>
            <TableHead>Role</TableHead>
            <TableHead></TableHead>
          </TableRow>
        </TableHeader>

        <TableBody>
          {members.map((member) => (
            <TableRow key={member.id}>
              <TableCell className="font-medium">{member.name}</TableCell>
              <TableCell className="text-muted-foreground">
                {member.email}
              </TableCell>
              <TableCell>{formatDate(member.joinedAt)}</TableCell>
              <TableCell>
                <MemberRole
                  clubId={clubId}
                  targetUserId={member.userId}
                  role={member.role}
                  canManageMembers={canManageMembers}
                />
              </TableCell>
              <TableCell>
                {canManageMembers && (
                  <RemoveClubMember
                    clubId={clubId}
                    targetUserId={member.userId}
                    userName={member.name}
                  />
                )}
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </div>
  );
}
