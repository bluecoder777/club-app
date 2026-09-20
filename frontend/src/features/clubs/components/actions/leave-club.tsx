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

import { toast } from '@/components/ui/toast';
import { useLeaveClub } from '../../api/use-leave-club';

type LeaveClubProps = {
  clubId: number;
  clubName?: string;
};

const LeaveClub = ({ clubId, clubName }: LeaveClubProps) => {
  const [open, setOpen] = React.useState(false);

  const { mutate, isPending } = useLeaveClub({
    clubId,

    mutationConfig: {
      onSuccess: () => {
        setOpen(false);
        toast.add({
          title: 'Successfully left club!',
        });
      },
      onError: () => {
        toast.add({
          title: 'Error leaving club!',
          type: 'error',
        });
      },
    },
  });

  const handleLeave = () => {
    mutate({ clubId });
  };

  return (
    <AlertDialog open={open} onOpenChange={setOpen}>
      <AlertDialogTrigger>
        <Button variant="destructive">Leave</Button>
      </AlertDialogTrigger>

      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>
            Leave {clubName ? `"${clubName}"` : 'this club'}?
          </AlertDialogTitle>

          <AlertDialogDescription>
            Are you sure you want to leave this club? You will no longer have
            access to the club as a member.
          </AlertDialogDescription>
        </AlertDialogHeader>

        <AlertDialogFooter>
          <AlertDialogCancel disabled={isPending}>Cancel</AlertDialogCancel>

          <AlertDialogAction
            onClick={(event) => {
              event.preventDefault();
              handleLeave();
            }}
            disabled={isPending}
            className="bg-destructive text-destructive-foreground hover:bg-destructive/90"
          >
            {isPending ? 'Leaving...' : 'Leave'}
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
};

export default LeaveClub;
