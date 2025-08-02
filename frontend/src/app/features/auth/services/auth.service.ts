import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { computed, inject, Injectable, signal, WritableSignal } from '@angular/core';
import { State } from '../../../core/models/state.model';
import { RegisterUserDTO } from '../models/auth.model';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private http = inject(HttpClient);

  private registerUser$: WritableSignal<State<void>> = signal(State.Builder<void>().forInit());
  public registerUserSig = computed(() => this.registerUser$());

  register(request: RegisterUserDTO): void {
    this.http.post<void>(`${environment?.API_URL}/auth/register`, request).subscribe({
      next: () => {
        this.registerUser$.set(State.Builder<void>().forSuccess());
      },
      error: (error: HttpErrorResponse) => {
        this.registerUser$.set(State.Builder<void>().forError(error));
      },
    });
  }

  public resetRegister(): void {
    this.registerUser$.set(State.Builder<void>().forInit());
  }
}
