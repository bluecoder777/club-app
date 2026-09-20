import { Skeleton } from '@/components/ui/skeleton';
import {
  Empty,
  EmptyDescription,
  EmptyHeader,
  EmptyTitle,
} from '@/components/ui/empty';
import { FileText } from 'lucide-react';
import type { DashboardPost } from '../../types';
import { EditPost } from '../actions/edit-post';

type PostListProps = {
  posts?: DashboardPost[];
  isLoading?: boolean;
  canManagePosts: boolean;
};

export function PostListSkeleton() {
  return (
    <div className="flex flex-col gap-3">
      {Array.from({ length: 3 }).map((_, index) => (
        <div key={index} className="bg-muted/30 rounded-xl p-4">
          <Skeleton className="mb-2 h-5 w-48" />
          <Skeleton className="h-4 w-full" />
          <Skeleton className="mt-1 h-4 w-3/4" />
        </div>
      ))}
    </div>
  );
}

export function PostList({
  posts = [],
  isLoading = false,
  canManagePosts,
}: PostListProps) {
  if (isLoading) {
    return <PostListSkeleton />;
  }

  if (!posts.length) {
    return (
      <Empty className="min-h-40 rounded-xl border">
        <EmptyHeader>
          <FileText className="text-muted-foreground size-10" />
          <EmptyTitle>No posts yet</EmptyTitle>
          <EmptyDescription>
            There are no dashboard posts for this club yet.
          </EmptyDescription>
        </EmptyHeader>
      </Empty>
    );
  }

  return (
    <div className="flex flex-col gap-3">
      {posts.map((post) => (
        <div
          key={post.id}
          className="bg-muted/30 flex flex-col gap-2 rounded-xl p-4"
        >
          <div className="flex items-start justify-between gap-4">
            <div className="min-w-0 flex-1">
              <h3 className="truncate text-sm font-semibold">{post.title}</h3>
              <p className="text-muted-foreground mt-1 text-sm leading-5">
                {post.description}
              </p>
            </div>
            <div className="shrink-0">
              {canManagePosts && <EditPost post={post} />}{' '}
            </div>
          </div>
          <div className="text-muted-foreground flex items-center gap-2 text-xs">
            <span>By {post.createdBy.name}</span>
            {post.updatedAt !== post.createdAt && (
              <>
                <span>•</span>
                <span>Updated by {post.lastUpdatedBy.name}</span>
              </>
            )}
          </div>
        </div>
      ))}
    </div>
  );
}
