import { Routes } from '@angular/router';

export const routes: Routes = [

  // 🔐 Rutas de autenticación
  {
    path: 'auth',
    loadChildren: () =>
      import('./auth/auth.routes').then((m) => m.authRoutes),
  },

  // 🛠️ Rutas del administrador (Lazy Loading)
  {
    path: 'admin',
    loadChildren: () =>
      import('./admin-dashboard/admin-dashboard.routes'),
  },

  // 🎵 Rutas del usuario con dashboard
  {
    path: 'dashboard',
    loadComponent: () =>
      import('./songs/pages/dasboard-page/dasboard-page'),
    children: [
      {
        path: 'trending',
        loadComponent: () =>
          import('./songs/pages/trending-page/trending-page'),
      },
      {
        path: 'amigos',
        loadComponent: () =>
          import('./songs/pages/amigos-page/amigos-page')
            .then(m => m.default),
      },
      {
        path: 'search',
        loadComponent: () =>
          import('./songs/pages/search/search'),
      },
      {
        path: 'searchAvanzada',
        loadComponent: () =>
          import('./songs/pages/busqueda-avanzada-page/busqueda-avanzada-page'),
      },
      {
        path: 'history/:query',
        loadComponent: () =>
          import('./songs/pages/song-history/song-history'),
      },
      {
        path: 'favorites',
        loadComponent: () =>
          import('./songs/pages/favorites-page/favorites-page'),
      },
      {
        path: 'discovery',
        loadComponent: () =>
          import('./songs/pages/discovery-page/discovery-page'),
      },
      {
        path: 'radio',
        loadComponent: () =>
          import('./songs/pages/radio-page/radio-page'),
      },
      {
        path: 'users-suggestions',
        loadComponent: () =>
          import('./songs/pages/users-suggestions-page/users-suggestions-page'),
      },
      {
        path: 'favorites-report',
        loadComponent: () =>
          import('./songs/pages/favorites-report-page/favorites-report-page'),
      },
      {
        path: 'profile',
        loadComponent: () =>
          import('./songs/pages/profile-page/profile-page'),
      },
      {
        path: '**',
        redirectTo: 'trending',
      },
    ],
  },

  // 👉 Cuando el usuario entra al front sin ruta → login
  {
    path: '',
    redirectTo: 'auth/login',
    pathMatch: 'full',
  },

  // 👉 Cualquier ruta no encontrada → login
  {
    path: '**',
    redirectTo: 'auth/login',
  },
];
