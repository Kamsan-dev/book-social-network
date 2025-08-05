import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'signin',
    pathMatch: 'full',
  },
  {
    path: 'signup',
    loadComponent: () => import('./features/auth/pages/auth-page/auth-page.component').then((c) => c.AuthPageComponent),
  },

  {
    path: 'activate-account',
    loadComponent: () => import('./features/auth/pages/verify-email/verify-email.component').then((c) => c.VerifyEmailComponent),
  },
];
