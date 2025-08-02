import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'signin',
    pathMatch: 'full',
  },
  {
    path: 'signup',
    loadComponent: () => import('./features/auth/pages/register-page/register-page.component').then((c) => c.RegisterPageComponent),
  },
];
