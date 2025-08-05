import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, EventEmitter, inject, input, Output, WritableSignal } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { AuthenticationFormDTO } from '../../models/auth.model';
import { StatusMessage } from '../../../../shared/utils/utils.model';
import { MessageModule } from 'primeng/message';

@Component({
  selector: 'app-login-form',
  standalone: true,
  imports: [ReactiveFormsModule, ButtonModule, CommonModule, MessageModule],
  templateUrl: './login-form.component.html',
  styleUrl: './login-form.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LoginFormComponent {
  fb = inject(FormBuilder);
  form!: FormGroup;
  loading = input.required<boolean>();

  message = input<StatusMessage | null>(null);

  @Output()
  loginChange = new EventEmitter<AuthenticationFormDTO>();

  @Output()
  showRegisterPage = new EventEmitter<boolean>();

  loginRequest!: AuthenticationFormDTO;

  public ngOnInit(): void {
    this.initForm();
  }

  private initForm(): void {
    this.form = this.fb.nonNullable.group({
      email: ['john-doe@email.com', [Validators.required, Validators.email]],
      password: ['password', Validators.required],
    });
  }

  public onSubmitLogin(): void {
    if (this.form.invalid) return;
    this.loginChange.emit(this.form.value as AuthenticationFormDTO);
  }

  public onSignUpLinkClick(event: MouseEvent | TouchEvent): void {
    event.stopPropagation();
    this.showRegisterPage.emit(true);
  }

  get f(): { [key: string]: AbstractControl } {
    return this.form.controls;
  }
}
