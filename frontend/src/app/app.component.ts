import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, OnInit, signal } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';
import { finalize } from 'rxjs';
import { UserDTO } from './features/auth/models/auth.model';
import { AuthService } from './features/auth/services/auth.service';
import { ToastService } from './layout/toast.service';

@Component({
  selector: 'app-root',
  standalone: true,
  providers: [MessageService],
  imports: [RouterOutlet, ToastModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent implements OnInit {
  private toastService: ToastService = inject(ToastService);
  private messageService = inject(MessageService);
  private authService = inject(AuthService);
  private router = inject(Router);

  loading = signal(false);

  public ngOnInit(): void {
    //this.initFontAwesome();
    this.listenToastService();
    this.loadUserProfile();
  }

  private listenToastService(): void {
    this.toastService.sendSub.subscribe({
      next: (newMessage) => {
        if (newMessage && newMessage.summary !== this.toastService.INIT_STATE) {
          this.messageService.add(newMessage);
        }
      },
    });
  }

  private loadUserProfile(): void {
    if (this.authService.isAuthenticated()) return;

    this.loading.set(true);
    this.authService
      .profile()
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (user: UserDTO) => {
          this.authService.userSig.set(user);
          console.log('is authenticated : ', this.authService.isAuthenticated());
        },
        error: (error: HttpErrorResponse) => {
          console.error('Profile load failed:', error);
          this.authService.clearUserData();
          this.router.navigateByUrl('auth');
        },
      });
  }
}
