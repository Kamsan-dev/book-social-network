import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { computed, inject, Injectable, signal, WritableSignal } from '@angular/core';
import { State } from '../../../core/models/state.model';
import { AccountValidationDTO, RegisterUserDTO, TokenValidationDTO } from '../models/auth.model';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private http = inject(HttpClient);

  private registerUser$: WritableSignal<State<void>> = signal(State.Builder<void>().forInit());
  registerUserSig = computed(() => this.registerUser$());

  private tokenValidation$: WritableSignal<State<TokenValidationDTO>> = signal(State.Builder<TokenValidationDTO>().forInit());
  tokenValidationSig = computed(() => this.tokenValidation$());

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

  validateUserAccount(request: AccountValidationDTO): void {
    this.http.post<TokenValidationDTO>(`${environment?.API_URL}/auth/account-validation`, request).subscribe({
      next: (response: TokenValidationDTO) => {
        this.tokenValidation$.set(State.Builder<TokenValidationDTO>().forSuccess(response));
      },
      error: (error: HttpErrorResponse) => {
        this.tokenValidation$.set(State.Builder<TokenValidationDTO>().forError(error));
      },
    });
  }

  resetTokenValidation(): void {
    this.tokenValidation$.set(State.Builder<TokenValidationDTO>().forInit());
  }
  resetRegister(): void {
    this.registerUser$.set(State.Builder<void>().forInit());
  }
}
