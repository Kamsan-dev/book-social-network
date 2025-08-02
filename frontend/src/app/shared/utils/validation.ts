import { AbstractControl, ValidatorFn } from '@angular/forms';

export default class Validation {
  static match(controlName: string, checkControlName: string): ValidatorFn {
    return (controls: AbstractControl) => {
      const control = controls.get(controlName);
      const checkControl = controls.get(checkControlName);

      // If either control is missing, return null (no error) to avoid breaking the form
      if (!control || !checkControl) {
        return null;
      }

      // Compare the values
      if (control.value !== checkControl.value) {
        // Set the error on the checkControl (e.g., confirmPassword)
        checkControl.setErrors({ ...checkControl.errors, matching: true });
        // Return error for the form group
        return { matching: true };
      } else {
        // Clear the 'matching' error if it exists, but preserve other errors
        if (checkControl.errors && checkControl.errors['matching']) {
          const errors = { ...checkControl.errors };
          delete errors['matching'];
          checkControl.setErrors(Object.keys(errors).length ? errors : null);
        }
        // Return null (no error) for the form group
        return null;
      }
    };
  }
}
