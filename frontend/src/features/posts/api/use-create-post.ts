import { api } from '@/lib/api-client';
import type { MutationConfig } from '@/lib/react-query';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { POST_ENDPOINTS } from '../config/endpoints';
import type { PostFormData } from '../schema/post';
import { getPostsQueryOptions } from './use-posts';

type CreatePostFormData = PostFormData & {
  clubId: number;
};

function createPost(data: CreatePostFormData): Promise<CreatePostFormData> {
  return api.post(POST_ENDPOINTS.DASHBOARD_POSTS_URL, data);
}

type UseCreatePostOptions = {
  mutationConfig?: MutationConfig<typeof createPost>;
  clubId: number;
};

export const useCreatePost = ({
  mutationConfig,
  clubId,
}: UseCreatePostOptions) => {
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
    mutationFn: createPost,
  });
};
