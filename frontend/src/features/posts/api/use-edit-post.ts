import { api } from '@/lib/api-client';
import type { MutationConfig } from '@/lib/react-query';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { POST_ENDPOINTS } from '../config/endpoints';
import type { PostFormData } from '../schema/post';
import { getPostsQueryOptions } from './use-posts';

type EditPostData = PostFormData & {
  postId: number;
};

function editPost(data: EditPostData): Promise<EditPostData> {
  return api.patch(POST_ENDPOINTS.DASHBOARD_POSTS_URL, data);
}

type UseEditPostOptions = {
  mutationConfig?: MutationConfig<typeof editPost>;
  clubId: number;
};

export const useEditPost = ({ mutationConfig, clubId }: UseEditPostOptions) => {
  const queryClient = useQueryClient();

  const { onSuccess, ...restConfig } = mutationConfig || {};
  return useMutation({
    onSuccess: (...args) => {
      queryClient.invalidateQueries({
        queryKey: getPostsQueryOptions(clubId).queryKey,
      });
      onSuccess?.(...args);
    },
    ...restConfig,
    mutationFn: editPost,
  });
};
