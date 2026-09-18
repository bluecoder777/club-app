import {
  Field,
  FieldDescription,
  FieldError,
  FieldLabel,
} from '@/components/ui/field';
import {
  Controller,
  type Control,
  type ControllerRenderProps,
  type FieldPath,
  type FieldValues,
} from 'react-hook-form';

type FormFieldWrapperProps<TFieldValues extends FieldValues> = {
  control: Control<TFieldValues>;
  name: FieldPath<TFieldValues>;
  label?: string | undefined;
  description?: string | undefined;
  render: (field: ControllerRenderProps<TFieldValues>) => React.ReactNode;
};

export function FormFieldWrapper<TFieldValues extends FieldValues>({
  control,
  name,
  label,
  description,
  render,
}: FormFieldWrapperProps<TFieldValues>) {
  return (
    <Controller
      name={name}
      control={control}
      render={({ field, fieldState }) => (
        <Field>
          {label && <FieldLabel htmlFor={name}>{label}</FieldLabel>}
          {render(field)}
          {description && <FieldDescription>{description}</FieldDescription>}
          <FieldError>{fieldState.error?.message}</FieldError>
        </Field>
      )}
    />
  );
}
