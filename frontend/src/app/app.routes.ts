import { Routes } from '@angular/router';
import { DashboardComponent } from './layout/components/dashboard/dashboard.component';
import { authorityRouteAccess } from './core/guards/authority-route-access.guard';
import { LogoutComponent } from './features/auth/components/logout/logout.component';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full',
  },
  {
    path: 'auth',
    loadComponent: () => import('./features/auth/pages/auth-page/auth-page.component').then((c) => c.AuthPageComponent),
  },

  {
    path: 'activate-account',
    loadComponent: () => import('./features/auth/components/verify-email/verify-email.component').then((c) => c.VerifyEmailComponent),
  },
  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [authorityRouteAccess],
    data: {
      authorities: ['ROLE_ADMIN'],
    },
  },
  {
    path: 'logout',
    component: LogoutComponent,
  },
];
