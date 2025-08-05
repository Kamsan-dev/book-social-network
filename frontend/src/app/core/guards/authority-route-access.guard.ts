import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, Router, RouterStateSnapshot } from '@angular/router';
import { AuthService } from '../../features/auth/services/auth.service';

export const authorityRouteAccess: CanActivateFn = (next: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const connectedUser = authService.userSig();
  if (authService.isAuthenticated() && connectedUser) {
    const authorities = next.data['authorities'];
    return !authorities || authorities.length === 0 || authService.hasAnyAuthority(authorities);
  } else {
    router.navigateByUrl('/auth');
    return false;
  }
};
