import { InputFormField } from '@/components/form/input-form-field';
import { TextAreaFormField } from '@/components/form/text-area-form-field';
import { FieldGroup } from '@/components/ui/field';
import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import { postSchema, type PostFormData } from '../../schema/post';

const PostForm = ({
  onSubmit,
  defaultValues,
}: {
  onSubmit: (data: PostFormData) => void;
  defaultValues?: Partial<PostFormData>;
}) => {
  const form = useForm<PostFormData>({
    resolver: zodResolver(postSchema),
    defaultValues: {
      title: defaultValues?.title ?? '',
      description: defaultValues?.description ?? '',
    },
  });

  return (
    <form id="post-form" onSubmit={form.handleSubmit(onSubmit)}>
      <FieldGroup>
        <InputFormField control={form.control} name="title" label="Title" />
        <TextAreaFormField
          control={form.control}
          name="description"
          label="Description"
        />
      </FieldGroup>
    </form>
  );
};

export default PostForm;
