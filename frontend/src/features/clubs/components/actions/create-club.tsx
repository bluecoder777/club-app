import { Button } from '@/components/ui/button';
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog';
import { toast } from '@/components/ui/toast';
import { Plus } from 'lucide-react';
import { useCreateClub } from '../../api/use-create-club';
import ClubForm from './club-form';
import { useNavigate } from 'react-router';
import { paths } from '@/config/paths';

export function CreateClub() {
  const navigate = useNavigate();

  const createClub = useCreateClub({
    mutationConfig: {
      onSuccess: (data) => {
        toast.add({
          title: 'Club created successfully!',
          type: 'success',
        });
        navigate(paths.club.getHref(data.data.id.toString()));
      },
      onError: () => {
        toast.add({
          title: 'Error creating club!',
          type: 'error',
        });
      },
    },
  });

  return (
    <Dialog>
      <DialogTrigger>
        <Button>
          <Plus /> New Club
        </Button>
      </DialogTrigger>
      <DialogContent className="sm:max-w-sm">
        <DialogHeader>
          <DialogTitle>Create Club</DialogTitle>
          <DialogDescription>
            Fill in the details to create a new club.
          </DialogDescription>
        </DialogHeader>
        <ClubForm onSubmit={createClub.mutate} />
        <DialogFooter>
          <DialogClose render={<Button variant="outline">Cancel</Button>} />
          <Button type="submit" form="club-form">
            Create
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
