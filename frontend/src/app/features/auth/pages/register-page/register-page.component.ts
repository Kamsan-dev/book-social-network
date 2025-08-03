import { ChangeDetectionStrategy, Component, effect, inject, OnInit, signal } from '@angular/core';
import { RegisterFormComponent } from '../../components/register-form/register-form.component';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { ToastService } from '../../../../layout/toast.service';
import { RegisterUserDTO } from '../../models/auth.model';

@Component({
  selector: 'app-register-page',
  standalone: true,
  imports: [RegisterFormComponent, CommonModule, RouterModule],
  templateUrl: './register-page.component.html',
  styleUrl: './register-page.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RegisterPageComponent implements OnInit {
  loading = signal(false);
  authService = inject(AuthService);
  toastService = inject(ToastService);
  registerSuccess = signal<boolean | null>(null);

  constructor() {
    this.listenToRegisterEffect();
  }

  public ngOnInit(): void {}

  private listenToRegisterEffect(): void {
    effect(
      () => {
        let state = this.authService.registerUserSig();
        if (state.status === 'OK') {
          this.loading.set(false);
          this.registerSuccess.set(true);
          this.authService.resetRegister();
          this.toastService.send({
            severity: 'success',
            summary: 'Success',
            detail: 'Account created. Please check your email to enable it.',
          });
        } else if (state.status === 'ERROR') {
          this.loading.set(false);
          this.registerSuccess.set(false);
          this.authService.resetRegister();
          this.toastService.send({
            severity: 'error',
            summary: 'Error',
            detail: 'Something went wrong, please try again.',
          });
        }
      },
      { allowSignalWrites: true }
    );
  }

  public onRegister(request: RegisterUserDTO): void {
    this.loading.set(true);
    console.log('toast');
    this.authService.register(request);
  }
}
