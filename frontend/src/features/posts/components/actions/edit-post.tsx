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
import { useEditPost } from '../../api/use-edit-post';
import type { DashboardPost } from '../../types';
import PostForm from './post-form';

type EditPostProps = {
  post: DashboardPost;
};

export function EditPost({ post }: EditPostProps) {
  const [open, setOpen] = useState(false);

  const editPost = useEditPost({
    clubId: post.clubId,
    mutationConfig: {
      onSuccess: () => {
        toast.add({
          title: 'Post updated successfully!',
          type: 'success',
        });
        setOpen(false);
      },
      onError: () => {
        toast.add({
          title: 'Error updating post!',
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
          <span className="hidden sm:inline">Edit Post</span>
        </Button>
      </DialogTrigger>
      <DialogContent className="sm:max-w-sm">
        <DialogHeader>
          <DialogTitle>Edit Post</DialogTitle>
          <DialogDescription>
            Fill in the details to edit the post.
          </DialogDescription>
        </DialogHeader>
        <PostForm
          onSubmit={(data) => {
            editPost.mutate({
              ...data,
              postId: post.id,
            });
          }}
          defaultValues={{
            title: post.title,
            description: post.description,
          }}
        />
        <DialogFooter>
          <DialogClose render={<Button variant="outline">Cancel</Button>} />
          <Button type="submit" form="post-form">
            Save
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
