import { usePosts } from '../../api/use-posts';
import { PostList, PostListSkeleton } from './post-list';
import { CreatePost } from '../actions/create-post';
import { useClub } from '@/features/clubs/api/use-club';
import { permissions } from '@/utils/permissions';

type PostsProps = {
  clubId: number;
};

export function Posts({ clubId }: PostsProps) {
  const postsQuery = usePosts({ clubId });
  const clubQuery = useClub({ clubId });

  if (postsQuery.isLoading || clubQuery.isLoading) {
    return <PostListSkeleton />;
  }

  const canManagePosts = !!(
    clubQuery.data?.data &&
    clubQuery.data?.data.currentUserRole &&
    permissions.createPost(clubQuery.data?.data.currentUserRole)
  );

  return (
    <div className="flex flex-col gap-4 rounded-2xl bg-white p-4">
      <div className="flex items-center justify-between">
        <h2 className="text-lg font-semibold">Dashboard Posts</h2>
        {canManagePosts && <CreatePost clubId={clubId} />}
      </div>
      <PostList
        posts={postsQuery.data?.data ?? []}
        isLoading={postsQuery.isLoading}
        canManagePosts={canManagePosts}
      />
    </div>
  );
}
