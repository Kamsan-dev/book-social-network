import { ChangeDetectionStrategy, Component, effect, inject, signal } from '@angular/core';
import { ButtonModule } from 'primeng/button';
import { AuthService } from '../../services/auth.service';
import { ToastService } from '../../../../layout/toast.service';

@Component({
  selector: 'app-logout',
  standalone: true,
  imports: [ButtonModule],
  templateUrl: './logout.component.html',
  styleUrl: './logout.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LogoutComponent {
  authService = inject(AuthService);

  loading = signal(false);

  toastService = inject(ToastService);

  constructor() {
    this.listenToLogout();
  }

  private listenToLogout(): void {
    effect(
      () => {
        let state = this.authService.logoutUserSig();
        if (state.status === 'OK') {
          this.loading.set(false);
          this.authService.resetLogout();
          this.toastService.send({
            severity: 'success',
            summary: 'Success',
            detail: 'Logged out successfully.',
          });
        } else if (state.status === 'ERROR') {
          this.loading.set(false);
          this.authService.resetLogout();
          this.toastService.send({
            severity: 'error',
            summary: 'Error',
            detail: 'Something went wrong when disconneting from the server.',
          });
        }
      },
      { allowSignalWrites: true }
    );
  }

  onTerminateSessionClick(event: MouseEvent | TouchEvent): void {
    event.stopImmediatePropagation();
    this.authService.logout();
    this.loading.set(true);
  }
}
