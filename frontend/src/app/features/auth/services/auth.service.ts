import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { computed, inject, Injectable, signal, WritableSignal } from '@angular/core';
import { JwtHelperService } from '@auth0/angular-jwt';
import { environment } from '../../../../environments/environment';
import { State } from '../../../core/models/state.model';
import { AccountValidationDTO, AuthenticationFormDTO, AuthenticationSuccessDTO, RegisterUserDTO, TokenValidationDTO, UserDTO } from '../models/auth.model';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private http = inject(HttpClient);
  private jwtHelper = new JwtHelperService();

  private registerUser$: WritableSignal<State<void>> = signal(State.Builder<void>().forInit());
  registerUserSig = computed(() => this.registerUser$());

  private tokenValidation$: WritableSignal<State<TokenValidationDTO>> = signal(State.Builder<TokenValidationDTO>().forInit());
  tokenValidationSig = computed(() => this.tokenValidation$());

  private loginUser$: WritableSignal<State<AuthenticationSuccessDTO>> = signal(State.Builder<AuthenticationSuccessDTO>().forInit());
  loginUserSig = computed(() => this.loginUser$());

  user: WritableSignal<UserDTO | null> = signal(null);

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

  loginUser(request: AuthenticationFormDTO): void {
    this.http.post<AuthenticationSuccessDTO>(`${environment?.API_URL}/auth/authenticate`, request).subscribe({
      next: (response: AuthenticationSuccessDTO) => {
        this.loginUser$.set(State.Builder<AuthenticationSuccessDTO>().forSuccess(response));
      },
      error: (error: HttpErrorResponse) => {
        this.loginUser$.set(State.Builder<AuthenticationSuccessDTO>().forError(error));
      },
    });
  }

  resetTokenValidation(): void {
    this.tokenValidation$.set(State.Builder<TokenValidationDTO>().forInit());
  }
  resetRegister(): void {
    this.registerUser$.set(State.Builder<void>().forInit());
  }

  resetLogin(): void {
    this.loginUser$.set(State.Builder<AuthenticationSuccessDTO>().forInit());
  }
}
