import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { computed, inject, Injectable, signal, WritableSignal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { State } from '../../../core/models/state.model';
import { AccountValidationDTO, AuthenticationFormDTO, AuthenticationSuccessDTO, RegisterUserDTO, TokenValidationDTO, UserDTO } from '../models/auth.model';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);

  private registerUser$: WritableSignal<State<void>> = signal(State.Builder<void>().forInit());
  registerUserSig = computed(() => this.registerUser$());

  private tokenValidation$: WritableSignal<State<TokenValidationDTO>> = signal(State.Builder<TokenValidationDTO>().forInit());
  tokenValidationSig = computed(() => this.tokenValidation$());

  private loginUser$: WritableSignal<State<UserDTO>> = signal(State.Builder<UserDTO>().forInit());
  loginUserSig = computed(() => this.loginUser$());

  private logoutUser$: WritableSignal<State<{ message: string }>> = signal(State.Builder<{ message: string }>().forInit());
  logoutUserSig = computed(() => this.logoutUser$());

  private profileUser$: WritableSignal<State<UserDTO>> = signal(State.Builder<UserDTO>().forInit());
  profileUserSig = computed(() => this.profileUser$);

  userSig: WritableSignal<UserDTO | null> = signal(null);

  isAuthenticated = computed(() => this.userSig() != null);

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
    this.http.post<UserDTO>(`${environment?.API_URL}/auth/authenticate`, request).subscribe({
      next: (response: UserDTO) => {
        this.loginUser$.set(State.Builder<UserDTO>().forSuccess(response));
        this.userSig.set(response);
        this.router.navigateByUrl('/dashboard');
      },
      error: (error: HttpErrorResponse) => {
        this.loginUser$.set(State.Builder<UserDTO>().forError(error));
      },
    });
  }

  logout(): void {
    this.clearUserData();
    this.http.post<{ message: string }>(`${environment?.API_URL}/logout`, {}).subscribe({
      next: (response: { message: string }) => {
        this.logoutUser$.set(State.Builder<{ message: string }>().forSuccess(response));
        this.router.navigateByUrl('auth');
      },
      error: (error: HttpErrorResponse) => {
        this.logoutUser$.set(State.Builder<{ message: string }>().forError(error));
      },
    });
  }

  profile(): Observable<UserDTO> {
    return this.http.get<UserDTO>(`${environment.API_URL}/user/profile`);
  }

  resetTokenValidation(): void {
    this.tokenValidation$.set(State.Builder<TokenValidationDTO>().forInit());
  }
  resetRegister(): void {
    this.registerUser$.set(State.Builder<void>().forInit());
  }

  resetLogin(): void {
    this.loginUser$.set(State.Builder<UserDTO>().forInit());
  }

  resetLogout(): void {
    this.logoutUser$.set(State.Builder<{ message: string }>().forInit());
  }

  hasAnyAuthority(authorities: string | Array<String>): boolean {
    if (!this.isAuthenticated()) return false;

    if (!Array.isArray(authorities)) {
      authorities = [authorities];
    }

    return authorities.some((authority) => this.userSig()?.roles?.includes(authority));
  }

  public clearUserData(): void {
    this.userSig.set(null);
  }
}
