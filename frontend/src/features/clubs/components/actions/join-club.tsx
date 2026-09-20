import { Button } from '@/components/ui/button';
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
import { toast } from '@/components/ui/toast';
import { useJoinClub } from '../../api/use-join-club';
import { useNavigate } from 'react-router';
import { paths } from '@/config/paths';

type JoinClubProps = {
  clubId: number;
  clubName?: string;
};

const JoinClub = ({ clubId, clubName }: JoinClubProps) => {
  const navigate = useNavigate();

  const { mutate, isPending } = useJoinClub({
    clubId,
    mutationConfig: {
      onSuccess: () => {
        toast.add({
          title: 'Successfully joined club!',
        });
        navigate(paths.club.getHref(clubId.toString()));
      },
      onError: () => {
        toast.add({
          title: 'Error joining club!',
          type: 'error',
        });
      },
    },
  });

  const handleJoin = () => {
    mutate({ clubId });
  };

  return (
    <AlertDialog>
      <AlertDialogTrigger
        render={
          <Button
            onClick={(e) => {
              e.stopPropagation();
            }}
            className="w-full"
          >
            Join Club
          </Button>
        }
      />
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>
            Join {clubName ? `"${clubName}"` : 'this club'}?
          </AlertDialogTitle>

          <AlertDialogDescription>
            Are you sure you want to join this club? You will become a member of
            the club once you confirm.
          </AlertDialogDescription>
        </AlertDialogHeader>

        <AlertDialogFooter>
          <AlertDialogCancel disabled={isPending}>Cancel</AlertDialogCancel>

          <AlertDialogAction onClick={handleJoin} disabled={isPending}>
            {isPending ? 'Joining...' : 'Join'}
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
};

export default JoinClub;
