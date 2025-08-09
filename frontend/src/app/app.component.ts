import { HttpErrorResponse } from '@angular/common/http';
import { Component, effect, inject, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';
import { finalize } from 'rxjs';
import { UserDTO } from './features/auth/models/auth.model';
import { AuthService } from './features/auth/services/auth.service';
import { MainComponent } from './layout/main/main.component';
import { NavbarComponent } from './layout/navbar/navbar.component';
import { SidebarComponent } from './layout/sidebar/sidebar.component';
import { ToastService } from './layout/toast.service';
import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { fontAwesomeIcons } from './shared/icons/font-awesome.icon';
import { SidebarService } from './layout/sidebar/sidebar.service';
import { ResponsiveService } from './shared/services/response.service';

@Component({
  selector: 'app-root',
  standalone: true,
  providers: [MessageService],
  imports: [ToastModule, NavbarComponent, SidebarComponent, MainComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent implements OnInit {
  private toastService: ToastService = inject(ToastService);
  private messageService = inject(MessageService);
  public authService = inject(AuthService);
  private router = inject(Router);
  private faIconLibrary = inject(FaIconLibrary);
  sidebarService = inject(SidebarService);
  responsiveService = inject(ResponsiveService);
  loading = signal(false);

  constructor() {
    this.listenToAuthenticatedUser();
  }

  public ngOnInit(): void {
    this.initFontAwesome();
    this.listenToastService();
    this.loadUserProfile();
  }

  private initFontAwesome(): void {
    this.faIconLibrary.addIcons(...fontAwesomeIcons);
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
        },
        error: (error: HttpErrorResponse) => {
          this.authService.clearUserData();
          this.router.navigateByUrl('auth');
          // todo toaster error
        },
      });
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
  private listenToAuthenticatedUser(): void {
    effect(() => {
      if (this.authService.isAuthenticated()) {
        document.body.classList.add('authenticated');
        document.body.classList.remove('unauthenticated');
      } else {
        document.body.classList.add('unauthenticated');
        document.body.classList.remove('authenticated');
      }
    });
  }
}
