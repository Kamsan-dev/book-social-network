import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { computed, inject, Injectable, signal, WritableSignal } from '@angular/core';
import { Router } from '@angular/router';
import { UserDTO } from '../../auth/models/auth.model';
import { createPaginationOption, Page, Pagination } from '../../../core/models/request.model';
import { State } from '../../../core/models/state.model';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private http = inject(HttpClient);
  private router = inject(Router);

  private users$: WritableSignal<State<Page<UserDTO>>> = signal(State.Builder<Page<UserDTO>>().forInit());
  usersSig = computed(() => this.users$());

  private updateProfileImage$: WritableSignal<State<{ profileImageId: string }>> = signal(State.Builder<{ profileImageId: string }>().forInit());
  updateProfileImageSig = computed(() => this.updateProfileImage$());

  getAll(pageRequest: Pagination): void {
    let params = createPaginationOption(pageRequest);
    this.http.get<Page<UserDTO>>(`${environment?.API_URL}/user/get-all`, { params }).subscribe({
      next: (response: Page<UserDTO>) => {
        this.users$.set(State.Builder<Page<UserDTO>>().forSuccess(response));
      },
      error: (error: HttpErrorResponse) => {
        this.users$.set(State.Builder<Page<UserDTO>>().forError(error));
      },
    });
  }

  updateProfileImage(formData: FormData): void {
    this.http.post<{ profileImageId: string }>(`${environment?.API_URL}/user/profile-image`, formData).subscribe({
      next: (response: { profileImageId: string }) => {
        this.updateProfileImage$.set(State.Builder<{ profileImageId: string }>().forSuccess(response));
      },
      error: (error: HttpErrorResponse) => {
        this.updateProfileImage$.set(State.Builder<{ profileImageId: string }>().forError(error));
      },
    });
  }

  resetUsers(): void {
    this.users$.set(State.Builder<Page<UserDTO>>().forInit());
  }

  resetUpdateProfileImage(): void {
    this.updateProfileImage$.set(State.Builder<{ profileImageId: string }>().forInit());
  }
}
