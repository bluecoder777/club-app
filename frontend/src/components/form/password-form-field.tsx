import { useState } from 'react';
import { Input } from '@/components/ui/input';
import { Eye, EyeOff } from 'lucide-react';
import type { Control, FieldPath, FieldValues } from 'react-hook-form';
import { FormFieldWrapper } from './form-field-wrapper';

type PasswordFormFieldProps<TFieldValues extends FieldValues> = {
  control: Control<TFieldValues>;
  name: FieldPath<TFieldValues>;
  label?: string;
  description?: string;
} & Omit<React.ComponentProps<typeof Input>, 'name' | 'defaultValue' | 'type'>;

export function PasswordFormField<TFieldValues extends FieldValues>({
  control,
  name,
  label,
  description,
  className,
  ...inputProps
}: PasswordFormFieldProps<TFieldValues>) {
  const [showPassword, setShowPassword] = useState(false);

  return (
    <FormFieldWrapper
      control={control}
      name={name}
      label={label}
      description={description}
      render={(field) => (
        <div className="relative">
          <Input
            {...field}
            {...inputProps}
            id={name}
            type={showPassword ? 'text' : 'password'}
            className={`pr-10 ${className ?? ''}`}
          />
          <button
            type="button"
            tabIndex={-1}
            onClick={() => setShowPassword((prev) => !prev)}
            className="text-muted-foreground hover:text-foreground absolute top-1/2 right-3 -translate-y-1/2"
          >
            {showPassword ? (
              <EyeOff className="h-4 w-4" />
            ) : (
              <Eye className="h-4 w-4" />
            )}
          </button>
        </div>
      )}
    />
  );
}
