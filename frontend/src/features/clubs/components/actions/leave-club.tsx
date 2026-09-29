import React from 'react';

import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from '@/components/ui/alert-dialog';
import { Button } from '@/components/ui/button';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';

import { toast } from '@/components/ui/toast';
import { useAuth } from '@/features/auth/components/auth-provider';
import { useMembers } from '@/features/members/api/use-members';
import type { Role } from '@/utils/permissions';
import { useLeaveClub } from '../../api/use-leave-club';
import { getErrorMessage } from '@/utils/get-error-message';

type LeaveClubProps = {
  clubId: number;
  clubName?: string;
  currentUserRole: Role | null;
  memberCount: number;
};

const LeaveClub = ({
  clubId,
  clubName,
  currentUserRole,
  memberCount,
}: LeaveClubProps) => {
  const [open, setOpen] = React.useState(false);
  const [successorUserId, setSuccessorUserId] = React.useState<number | null>(
    null,
  );
  const { user } = useAuth();
  const requiresHandover = currentUserRole === 'ADMIN' && memberCount > 1;

  const membersQuery = useMembers({
    clubId,
    queryConfig: {
      enabled: open && requiresHandover,
    },
  });

  const possibleSuccessors =
    membersQuery.data?.data.filter((member) => member.userId !== user?.id) ??
    [];

  const { mutate, isPending } = useLeaveClub({
    clubId,

    mutationConfig: {
      onSuccess: () => {
        setOpen(false);
        setSuccessorUserId(null);
        toast.add({
          title: 'Successfully left club!',
        });
      },
      onError: (error) => {
        toast.add({
          title: getErrorMessage(error, 'Error leaving club'),
          type: 'error',
        });
      },
    },
  });

  const handleLeave = () => {
    mutate({
      clubId,
      ...(successorUserId ? { successorUserId } : {}),
    });
  };

  const handleOpenChange = (nextOpen: boolean) => {
    setOpen(nextOpen);
    if (!nextOpen) {
      setSuccessorUserId(null);
    }
  };

  return (
    <AlertDialog open={open} onOpenChange={handleOpenChange}>
      <AlertDialogTrigger render={<Button variant="destructive" />}>
        Leave
      </AlertDialogTrigger>

      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>
            {requiresHandover
              ? 'Choose the next admin'
              : `Leave ${clubName ? `"${clubName}"` : 'this club'}?`}
          </AlertDialogTitle>

          <AlertDialogDescription>
            {requiresHandover
              ? 'An admin cannot leave while other members remain without handing over administration. Choose a member to become an admin before you leave.'
              : 'Are you sure you want to leave this club? You will no longer have access to the club as a member.'}
          </AlertDialogDescription>
        </AlertDialogHeader>

        {requiresHandover && (
          <div className="space-y-2">
            <label className="text-sm font-medium" htmlFor="successor-admin">
              New admin
            </label>
            <Select
              value={successorUserId?.toString() ?? ''}
              onValueChange={(value) =>
                setSuccessorUserId(value ? Number(value) : null)
              }
              disabled={membersQuery.isLoading || isPending}
            >
              <SelectTrigger id="successor-admin" className="w-full">
                <SelectValue
                  placeholder={
                    membersQuery.isLoading
                      ? 'Loading members...'
                      : 'Select a member'
                  }
                />
              </SelectTrigger>
              <SelectContent>
                {possibleSuccessors.map((member) => (
                  <SelectItem
                    key={member.userId}
                    value={member.userId.toString()}
                  >
                    {member.name} ({member.email})
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>
        )}

        <AlertDialogFooter>
          <AlertDialogCancel disabled={isPending}>Cancel</AlertDialogCancel>

          <AlertDialogAction
            onClick={(event) => {
              event.preventDefault();
              handleLeave();
            }}
            disabled={
              isPending ||
              membersQuery.isLoading ||
              (requiresHandover && !successorUserId)
            }
            className="bg-destructive text-destructive-foreground hover:bg-destructive/90"
          >
            {isPending
              ? 'Leaving...'
              : requiresHandover
                ? 'Hand over and leave'
                : 'Leave'}
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
};

export default LeaveClub;
