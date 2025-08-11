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

  private updateProfileImage$: WritableSignal<State<{ publicId: string }>> = signal(State.Builder<{ publicId: string }>().forInit());
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

  updateProfileImage(formData: FormData, publicUserId: string): void {
    this.http.post<{ publicId: string }>(`${environment?.API_URL}/user/users/${publicUserId}/profile-image`, formData).subscribe({
      next: (response: { publicId: string }) => {
        this.updateProfileImage$.set(State.Builder<{ publicId: string }>().forSuccess(response));
      },
      error: (error: HttpErrorResponse) => {
        this.updateProfileImage$.set(State.Builder<{ publicId: string }>().forError(error));
      },
    });
  }

  resetUsers(): void {
    this.users$.set(State.Builder<Page<UserDTO>>().forInit());
  }

  resetUpdateProfileImage(): void {
    this.updateProfileImage$.set(State.Builder<{ publicId: string }>().forInit());
  }
}
