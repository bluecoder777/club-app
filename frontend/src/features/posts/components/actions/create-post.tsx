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
import { useState } from 'react';
import { useCreatePost } from '../../api/use-create-post';
import PostForm from './post-form';

type CreatePostProps = {
  clubId: number;
};

export function CreatePost({ clubId }: CreatePostProps) {
  const [open, setOpen] = useState(false);

  const createPost = useCreatePost({
    clubId,
    mutationConfig: {
      onSuccess: () => {
        toast.add({
          title: 'Post created successfully!',
          type: 'success',
        });
        setOpen(false);
      },
      onError: () => {
        toast.add({
          title: 'Error creating post!',
          type: 'error',
        });
      },
    },
  });

  function handleOpenChange(nextOpen: boolean) {
    setOpen(nextOpen);
  }

  return (
    <Dialog open={open} onOpenChange={handleOpenChange}>
      <DialogTrigger>
        <Button>
          <Plus /> New Post
        </Button>
      </DialogTrigger>
      <DialogContent className="sm:max-w-sm">
        <DialogHeader>
          <DialogTitle>Create Post</DialogTitle>
          <DialogDescription>
            Fill in the details to create a new dashboard post.
          </DialogDescription>
        </DialogHeader>
        <PostForm
          onSubmit={(data) => {
            createPost.mutate({ ...data, clubId });
          }}
        />
        <DialogFooter>
          <DialogClose render={<Button variant="outline">Cancel</Button>} />
          <Button type="submit" form="post-form">
            Create
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
