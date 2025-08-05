import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, effect, ElementRef, inject, input, OnInit, QueryList, signal, ViewChildren } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { ToastService } from '../../../../layout/toast.service';
import { AccountValidationDTO, TokenValidationDTO } from '../../models/auth.model';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-verify-email',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, ButtonModule],
  templateUrl: './verify-email.component.html',
  styleUrl: './verify-email.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VerifyEmailComponent implements OnInit {
  loading = signal(false);

  authService = inject(AuthService);

  toastService = inject(ToastService);

  fb = inject(FormBuilder);

  form!: FormGroup;

  token = input.required<string>();

  expiresAt = input.required<string>();

  digits = Array(6).fill(null);

  request: AccountValidationDTO | undefined;

  showForm = signal(true);

  errorMessage = signal<string | null>(null);

  @ViewChildren('input') inputs!: QueryList<ElementRef<HTMLInputElement>>;

  constructor() {
    this.listenToValidateUserAccount();
  }

  ngOnInit(): void {
    if (this.isAccountTokenValidationExpired()) {
      this.showForm.set(false);
    }
    this.initForm();
  }

  onInput(event: Event, index: number): void {
    const input = event.target as HTMLInputElement;
    const value = input.value;

    if (/^\d$/.test(value)) {
      this.inputs.get(index + 1)?.nativeElement.focus();
    } else {
      this.form.controls[index]?.setValue('');
    }
  }

  onKeyDown(event: KeyboardEvent, index: number): void {
    const input = event.target as HTMLInputElement;
    if (event.key === 'Backspace' && index > 0) {
      this.inputs.get(index - 1)?.nativeElement.focus();
    }
  }

  onPaste(event: ClipboardEvent): void {
    event.preventDefault();
    const paste = event.clipboardData?.getData('text') ?? '';
    if (/^\d{6}$/.test(paste)) {
      // 000000
      paste.split('').forEach((digit, i) => {
        this.form.controls[i].setValue(digit);
      });
      this.inputs.get(5)?.nativeElement.focus();
    }
  }

  private listenToValidateUserAccount(): void {
    effect(
      () => {
        let state = this.authService.tokenValidationSig();
        if (state.value && state.status === 'OK') {
          this.loading.set(false);
          this.authService.resetTokenValidation();
          this.errorMessage.set(null);
          this.toastService.send({
            severity: 'success',
            summary: 'Success',
            detail: 'Your email has been verified successfully.',
          });
        } else if (state.status === 'ERROR') {
          this.loading.set(false);
          this.authService.resetTokenValidation();
          this.errorMessage.set(state.error?.error.message);
          this.toastService.send({
            severity: 'error',
            summary: 'Error',
            detail: state.error?.error.message,
          });
        }
      },
      { allowSignalWrites: true }
    );
  }

  private getCode(): string {
    return Object.values(this.form.value).join('');
  }

  private initForm(): void {
    this.form = this.fb.nonNullable.group({
      0: ['', Validators.required],
      1: ['', Validators.required],
      2: ['', Validators.required],
      3: ['', Validators.required],
      4: ['', Validators.required],
      5: ['', Validators.required],
    });
  }

  private isAccountTokenValidationExpired(): boolean {
    const date = new Date(this.expiresAt());
    if (date < new Date()) return true;
    else return false;
  }

  public onSubmitCode(): void {
    if (this.form.invalid) return;
    this.request = {
      code: this.getCode(),
      verificationToken: this.token(),
    };
    this.authService.validateUserAccount(this.request);
  }
}
