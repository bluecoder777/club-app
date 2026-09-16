import { Input } from '@/components/ui/input';
import type { Control, FieldPath, FieldValues } from 'react-hook-form';
import { FormFieldWrapper } from './form-field-wrapper';

type InputFormFieldProps<TFieldValues extends FieldValues> = {
  control: Control<TFieldValues>;
  name: FieldPath<TFieldValues>;
  label?: string;
  description?: string;
} & Omit<React.ComponentProps<typeof Input>, 'name' | 'defaultValue'>;

export function InputFormField<TFieldValues extends FieldValues>({
  control,
  name,
  label,
  description,
  ...inputProps
}: InputFormFieldProps<TFieldValues>) {
  return (
    <FormFieldWrapper
      control={control}
      name={name}
      label={label}
      description={description}
      render={(field) => <Input {...field} {...inputProps} id={name} />}
    />
  );
}
