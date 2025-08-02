import { Component, EventEmitter, inject, input, OnInit, Output } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import Validation from '../../../../shared/utils/validation';
import { RegisterUserDTO } from '../../models/auth.model';
import { CommonModule } from '@angular/common';
import { ButtonModule } from 'primeng/button';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-register-form',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, ButtonModule, RouterLink],
  templateUrl: './register-form.component.html',
  styleUrl: './register-form.component.scss',
})
export class RegisterFormComponent implements OnInit {
  fb = inject(FormBuilder);
  form!: FormGroup;
  loading = input.required<boolean>();

  @Output()
  registerChange = new EventEmitter<RegisterUserDTO>();

  registerRequest!: RegisterUserDTO;

  public ngOnInit(): void {
    this.initForm();
  }

  private initForm(): void {
    this.form = this.fb.nonNullable.group(
      {
        firstName: ['John', Validators.required],
        lastName: ['Doe', [Validators.required]],
        email: ['john-doe@email.com', [Validators.required, Validators.email]],
        password: ['password', [Validators.required, Validators.minLength(8)]],
        confirmPassword: ['', Validators.required],
        acceptTerms: [false, Validators.requiredTrue],
      },
      {
        validators: [Validation.match('password', 'confirmPassword')],
      }
    );
  }

  public onSubmitRegister(): void {
    if (this.form.invalid) return;
    console.log('toast');
    const { firstName, lastName, email, password }: RegisterUserDTO = this.form.value;
    this.registerRequest = {
      firstName,
      lastName,
      email,
      password,
    };

    this.registerChange.emit(this.registerRequest);
    console.log(this.registerRequest);
  }

  get f(): { [key: string]: AbstractControl } {
    return this.form.controls;
  }
}
