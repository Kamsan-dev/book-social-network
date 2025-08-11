import { ChangeDetectionStrategy, ChangeDetectorRef, Component, effect, inject, OnInit, signal } from '@angular/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Pagination } from '../../core/models/request.model';
import { UserDTO } from '../../features/auth/models/auth.model';
import { UserService } from '../../features/users/services/user.service';
import { UserCardComponent } from '../../features/users/user-card/user-card.component';
import { ToastService } from '../toast.service';
import { ViewUserDetailsComponent } from '../view-user-details/view-user-details.component';
import { AuthService } from '../../features/auth/services/auth.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [UserCardComponent, FontAwesomeModule, ViewUserDetailsComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DashboardComponent implements OnInit {
  userService = inject(UserService);
  toastService = inject(ToastService);
  authService = inject(AuthService);
  cdr = inject(ChangeDetectorRef);
  pageRequest: Pagination = { size: 20, page: 0, sort: [] };
  users = signal<Array<UserDTO> | undefined>(undefined);
  loading = signal(false);

  rightSidebarVisible = signal(false);
  userToShow = signal<UserDTO | null>(null);

  constructor() {
    this.listenToFetchUsers();
    this.listenToUserUpdatedImage();
  }

  ngOnInit(): void {
    this.loadUsers();
  }

  private loadUsers(): void {
    this.userService.getAll(this.pageRequest);
    this.loading.set(true);
  }

  private listenToFetchUsers(): void {
    effect(
      () => {
        let state = this.userService.usersSig();
        if (state.status === 'OK' && state.value) {
          this.loading.set(false);
          this.userService.resetUsers();
          this.users.set(state.value.content);
        } else if (state.status === 'ERROR') {
          this.userService.resetUsers();
          this.loading.set(false);
          this.toastService.send({
            severity: 'error',
            summary: 'Error',
            detail: 'Something went wrong when fetching users list',
          });
        }
      },
      { allowSignalWrites: true }
    );
  }

  private listenToUserUpdatedImage(): void {
    effect(
      () => {
        let state = this.userService.updateProfileImageSig();
        if (state.status === 'OK' && state.value) {
          this.updateProfileImageIdConnectedUser(state.value.publicId);
          this.userService.resetUpdateProfileImage();
          this.cdr.markForCheck();
        } else if (state.status === 'ERROR') {
          this.userService.resetUpdateProfileImage();
          this.toastService.send({
            severity: 'error',
            summary: 'Error',
            detail: 'Something went wrong when updating your profile image',
          });
        }
      },
      { allowSignalWrites: true }
    );
  }

  onUpdateUserClick(event: UserDTO) {
    this.userToShow.set(event);
    console.log(this.userToShow());
    this.rightSidebarVisible.set(true);
  }

  onSidebarVisibleChange(event: boolean) {
    this.rightSidebarVisible.set(event);
  }

  updateProfileImageIdConnectedUser(userPublicId: string): void {
    // Update connected user
    let currentUser = this.authService.userSig() as UserDTO;
    if (currentUser && currentUser.publicId === userPublicId) {
      this.authService.userSig.set({ ...currentUser });
    }

    // Update in users list
    this.users.update((users) => users?.map((u) => (u.publicId === userPublicId ? { ...u } : u)));

    this.userToShow.set({ ...(this.userToShow() as UserDTO) });
  }
}
