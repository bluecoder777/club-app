import { Textarea } from '@/components/ui/textarea';
import type { Control, FieldPath, FieldValues } from 'react-hook-form';
import { FormFieldWrapper } from './form-field-wrapper';

type TextAreaFormFieldProps<TFieldValues extends FieldValues> = {
  control: Control<TFieldValues>;
  name: FieldPath<TFieldValues>;
  label?: string;
  description?: string;
} & Omit<React.ComponentProps<typeof Textarea>, 'name' | 'defaultValue'>;

export function TextAreaFormField<TFieldValues extends FieldValues>({
  control,
  name,
  label,
  description,
  ...textareaProps
}: TextAreaFormFieldProps<TFieldValues>) {
  return (
    <FormFieldWrapper
      control={control}
      name={name}
      label={label}
      description={description}
      render={(field) => <Textarea {...field} {...textareaProps} id={name} />}
    />
  );
}
