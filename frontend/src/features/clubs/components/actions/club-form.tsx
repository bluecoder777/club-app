import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import { clubSchema, type ClubFormData } from '../../schema/club';
import { InputFormField } from '@/components/form/input-form-field';
import { FieldGroup } from '@/components/ui/field';
import { TextAreaFormField } from '@/components/form/text-area-form-field';

const ClubForm = ({
  onSubmit,
  defaultValues,
}: {
  onSubmit: (data: ClubFormData) => void;
  defaultValues?: ClubFormData;
}) => {
  const form = useForm<ClubFormData>({
    resolver: zodResolver(clubSchema),
    defaultValues: defaultValues ?? {
      name: '',
      description: '',
    },
  });

  return (
    <form id="club-form" onSubmit={form.handleSubmit(onSubmit)}>
      <FieldGroup>
        <InputFormField control={form.control} name="name" label="Name" />

        <TextAreaFormField
          control={form.control}
          name="description"
          label="Description"
        />
      </FieldGroup>
    </form>
  );
};

export default ClubForm;
