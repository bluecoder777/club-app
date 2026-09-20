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
import { useRemoveClubMember } from '../api/use-remove-club-member';

type RemoveClubMemberProps = {
  clubId: number;
  targetUserId: number;
  userName?: string;
};

const RemoveClubMember = ({
  clubId,
  targetUserId,
  userName,
}: RemoveClubMemberProps) => {
  const [open, setOpen] = React.useState(false);

  const { mutate, isPending } = useRemoveClubMember({
    mutationConfig: {
      onSuccess: () => {
        setOpen(false);

        toast.add({
          title: 'Member removed successfully!',
        });
      },
      onError: () => {
        toast.add({
          title: 'Error removing member!',
          type: 'error',
        });
      },
    },
  });

  const handleRemove = () => {
    mutate({
      clubId,
      targetUserId,
    });
  };

  return (
    <AlertDialog open={open} onOpenChange={setOpen}>
      <AlertDialogTrigger>
        <Button variant="destructive" size="sm">
          Remove
        </Button>
      </AlertDialogTrigger>

      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>
            Remove {userName ? `"${userName}"` : 'this member'}?
          </AlertDialogTitle>

          <AlertDialogDescription>
            Are you sure you want to remove this member from the club? They will
            no longer have access to the club as a member.
          </AlertDialogDescription>
        </AlertDialogHeader>

        <AlertDialogFooter>
          <AlertDialogCancel disabled={isPending}>Cancel</AlertDialogCancel>

          <AlertDialogAction
            onClick={(event) => {
              event.preventDefault();
              handleRemove();
            }}
            disabled={isPending}
            className="bg-destructive text-destructive-foreground hover:bg-destructive/90"
          >
            {isPending ? 'Removing...' : 'Remove'}
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
};

export default RemoveClubMember;
