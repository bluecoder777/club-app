import { useState } from 'react';
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
import { Pencil } from 'lucide-react';
import { useEditClub } from '../../api/use-edit-club';
import type { Club } from '../../types';
import ClubForm from './club-form';

export function EditClub({ club }: { club: Club }) {
  const [open, setOpen] = useState(false);

  const editClub = useEditClub({
    clubId: club.id,
    mutationConfig: {
      onSuccess: () => {
        toast.add({
          title: 'Club updated successfully!',
          type: 'success',
        });
        setOpen(false);
      },
      onError: () => {
        toast.add({
          title: 'Error updating club!',
          type: 'error',
        });
      },
    },
  });

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger>
        <Button variant="outline" className="gap-2">
          <Pencil />
          <span className="hidden sm:inline">Edit Club</span>
        </Button>
      </DialogTrigger>
      <DialogContent className="sm:max-w-sm">
        <DialogHeader>
          <DialogTitle>Edit Club</DialogTitle>
          <DialogDescription>
            Fill in the details to edit the club.
          </DialogDescription>
        </DialogHeader>
        <ClubForm
          onSubmit={(data) => {
            editClub.mutate({
              data,
              clubId: club.id,
            });
          }}
          defaultValues={{
            description: club.description,
            name: club.name,
          }}
        />
        <DialogFooter>
          <DialogClose render={<Button variant="outline">Cancel</Button>} />
          <Button type="submit" form="club-form">
            Save
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
