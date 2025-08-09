import { ChangeDetectionStrategy, Component, effect, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Pagination } from '../../../core/models/request.model';
import { UserDTO } from '../../../features/auth/models/auth.model';
import { UserService } from '../../../features/users/services/user.service';
import { UserCardComponent } from '../../../features/users/user-card/user-card.component';
import { ToastService } from '../../toast.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [RouterLink, UserCardComponent, FontAwesomeModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DashboardComponent implements OnInit {
  userService = inject(UserService);
  toastService = inject(ToastService);

  pageRequest: Pagination = { size: 20, page: 0, sort: [] };
  users = signal<Array<UserDTO> | undefined>(undefined);
  loading = signal(false);

  constructor() {
    this.listenToFetchUsers();
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
          //this.userService.resetUsers();
          this.users.set(state.value.content);
        } else if (state.status === 'ERROR') {
          //this.userService.resetUsers();
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
}
