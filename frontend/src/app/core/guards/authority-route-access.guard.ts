import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../../features/auth/services/auth.service';
import { catchError, map, of } from 'rxjs';

export const authorityRouteAccess: CanActivateFn = (next, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // If already authenticated, check authorities
  if (authService.isAuthenticated() && authService.userSig()) {
    const authorities = next.data['authorities'] || [];
    console.log('Guard: Already authenticated');
    return !authorities.length || authService.hasAnyAuthority(authorities);
  }

  return authService.profile().pipe(
    map((user) => {
      if (!user) {
        console.log('Guard: No user returned; redirecting to /auth');
        router.navigateByUrl('/auth');
        return false;
      }
      const authorities = next.data['authorities'] || [];
      return !authorities.length || authService.hasAnyAuthority(authorities);
    }),
    catchError((error) => {
      console.error('Guard: Profile load failed:', error);
      authService.clearUserData();
      router.navigateByUrl('/auth');
      return of(false);
    })
  );
};
