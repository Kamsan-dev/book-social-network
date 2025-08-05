import { ChangeDetectionStrategy, Component, effect, inject, OnInit, signal } from '@angular/core';
import { RegisterFormComponent } from '../../components/register-form/register-form.component';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { ToastService } from '../../../../layout/toast.service';
import { AuthenticationFormDTO, RegisterUserDTO } from '../../models/auth.model';
import { LoginFormComponent } from '../../components/login-form/login-form.component';
import { animate, group, query, style, transition, trigger } from '@angular/animations';
import { StatusMessage } from '../../../../shared/utils/utils.model';

@Component({
  selector: 'app-auth-page',
  standalone: true,
  imports: [RegisterFormComponent, CommonModule, RouterModule, LoginFormComponent],
  templateUrl: './auth-page.component.html',
  styleUrl: './auth-page.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  animations: [
    trigger('slideAnimation', [
      transition('* <=> *', [
        // Set initial styles on both components
        query(
          ':enter, :leave',
          [
            style({
              position: 'absolute',
              width: '100%',
              top: 0,
              left: 0,
            }),
          ],
          { optional: true }
        ),

        group([
          // Entering component slides in from right
          query(':enter', [style({ transform: 'translateX(100%)', opacity: 0 }), animate('400ms ease', style({ transform: 'translateX(0)', opacity: 1 }))], {
            optional: true,
          }),

          // Leaving component slides out to left
          query(':leave', [style({ transform: 'translateX(0)', opacity: 1 }), animate('400ms ease', style({ transform: 'translateX(-100%)', opacity: 0 }))], {
            optional: true,
          }),
        ]),
      ]),
    ]),
  ],
})
export class AuthPageComponent implements OnInit {
  loading = signal(false);
  authService = inject(AuthService);
  toastService = inject(ToastService);
  router = inject(Router);

  errorMessage = signal('');
  displayLoginForm = signal(true);
  statusMessage = signal<StatusMessage | null>(null);

  constructor() {
    this.listenToRegisterEffect();
    this.listenToLoginEffect();
  }

  public ngOnInit(): void {
    if (this.authService.isAuthenticated() && this.authService.userSig()) {
      this.router.navigateByUrl('/dashboard');
      return;
    }
  }

  private listenToRegisterEffect(): void {
    effect(
      () => {
        let state = this.authService.registerUserSig();
        if (state.status === 'OK') {
          this.loading.set(false);
          this.setStatusMessage("We've sent you an email to enable your account.", 'success');
          this.toastService.send({
            severity: 'success',
            summary: 'Success',
            detail: 'Account created. Please check your email to enable it.',
          });
        } else if (state.status === 'ERROR') {
          this.loading.set(false);
          this.authService.resetRegister();
          this.setStatusMessage('An error has occurred when trying to register your account. Please try again.', 'error');
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

  private listenToLoginEffect(): void {
    effect(
      () => {
        let state = this.authService.loginUserSig();
        if (state.status === 'OK' && state.value) {
          this.loading.set(false);
          this.authService.resetLogin();
          this.toastService.send({
            severity: 'success',
            summary: 'Success',
            detail: `Good morning ${state.value.firstName}`,
          });
        } else if (state.status === 'ERROR') {
          this.loading.set(false);
          this.authService.resetLogin();
          const errorCode = state.error?.status;
          this.setStatusMessage(state.error!.error.detail, 'error');
          if (errorCode == 500) {
            this.toastService.send({
              severity: 'error',
              summary: 'Error',
              detail: 'Something went wrong, please try again.',
            });
          }
        }
      },
      { allowSignalWrites: true }
    );
  }

  onRegister(request: RegisterUserDTO): void {
    this.loading.set(true);
    this.authService.register(request);
  }

  onLogin(request: AuthenticationFormDTO) {
    this.loading.set(true);
    this.authService.loginUser(request);
  }

  setDisplayLoginForm(bool: boolean) {
    this.statusMessage.set(null);
    this.displayLoginForm.set(bool);
  }

  private setStatusMessage(text: string, type: 'success' | 'error'): void {
    this.statusMessage.set({
      text,
      type,
    });
  }
}
