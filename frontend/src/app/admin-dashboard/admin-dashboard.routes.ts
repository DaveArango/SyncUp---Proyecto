import { Routes } from '@angular/router';

export const adminDashboardRoutes: Routes = [
  {
    path: 'dashboard',
    loadComponent: () =>
      import('./layouts/admin-dashboard-layout/admin-dashboard-layout.component')

  },
  {
    path: 'songs',
    loadComponent: () =>
      import('./pages/songs/songs-list/songs-list.component'),

  },
  {
    path: 'songst',
    loadComponent: () =>
      import('./pages/songs/songs-form/songs-form.component'),

  },

];
export default adminDashboardRoutes;
