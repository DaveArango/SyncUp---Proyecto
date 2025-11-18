import { Routes } from '@angular/router';

export const authRoutes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./layout/auth-layout/auth-layout').then(m => m.AuthLayout),
    children: [
      {
        path: 'login',
        loadComponent: () =>
          import('./pages/login-page/login-page').then(m => m.LoginPage)
      },
      {
        path: 'register',
        loadComponent: () =>
          import('./pages/register-page/register-page').then(m => m.RegisterPage)
      },
      {
        path: '**',
        redirectTo: 'login'
      }
    ]
  }
];

export default authRoutes;

