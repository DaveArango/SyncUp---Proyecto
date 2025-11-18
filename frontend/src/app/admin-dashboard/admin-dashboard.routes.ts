import { Routes } from '@angular/router';

export const adminDashboardRoutes: Routes = [
  {
    path: 'dashboard-admin',
    loadComponent: () =>
      import('./pages/dashboard-admin-page/dashboard-admin-page'),

    children:[
      {
        path: 'gestionar-canciones',
        loadComponent: () =>
          import('./pages/gestionar-canciones/gestionar-canciones/gestionar-canciones'),

      },
      {
        path: 'gestionar-usuarios',
        loadComponent: () =>
          import('./pages/gestionar-usuarios/gestionar-usuarios/gestionar-usuarios'),

      },
      {
        path: 'cargar-canciones',
        loadComponent: () =>
          import('./pages/cargar-canciones/cargar-canciones/cargar-canciones'),

      },
      {
        path: '**',
        redirectTo: 'gestionar-canciones',
      },

    ]

  },



];
export default adminDashboardRoutes;
