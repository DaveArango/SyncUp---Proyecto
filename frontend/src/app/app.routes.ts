import { Routes } from '@angular/router';
import { isNotAuthenticatedGuard } from './auth/guards/is-not-authenticated.guard';
import { authGuard } from './auth/guards/auth.guard';



export const routes: Routes = [

  {

    path: 'auth',
    //canActivate: [isNotAuthenticatedGuard],
    loadChildren: () => import('./auth/auth.routes'),
  },
    // Rutas de administrador
   // 🛠️ Rutas del administrador (Lazy Loaded)
  {
    path: 'admin',
    loadChildren: () =>
      import('./admin-dashboard/admin-dashboard.routes'),
  },


  {
    path: 'dashboard',
    //canActivate: [authGuard],
    loadComponent: () => import('./songs/pages/dasboard-page/dasboard-page'),
    children:[
       {
        path: 'trending',
        loadComponent: () => import('./songs/pages/trending-page/trending-page'),
      },
      {
        path: 'amigos',
        loadComponent: () =>
          import('./songs/pages/amigos-page/amigos-page')
            .then(m => m.default)
      },
      {
        path: 'search',
        loadComponent: () => import('./songs/pages/search/search'),
      },
      {
        path: 'searchAvanzada',
        loadComponent: () => import('./songs/pages/busqueda-avanzada-page/busqueda-avanzada-page'),
      },
      {
        path: 'history/:query',
        loadComponent: () => import('./songs/pages/song-history/song-history'),
      },
      {
        path: 'favorites',
        loadComponent: () => import('./songs/pages/favorites-page/favorites-page'),
      },
      {
        path: 'discovery',
        loadComponent: () => import('./songs/pages/discovery-page/discovery-page'),
      },
      {
        path: 'radio',
        loadComponent: () => import('./songs/pages/radio-page/radio-page'),
      },
      {
        path: 'users-suggestions',
        loadComponent: () => import('./songs/pages/users-suggestions-page/users-suggestions-page'),
      },
      {
        path: 'favorites-report',
        loadComponent: () => import('./songs/pages/favorites-report-page/favorites-report-page'),
      },
      {
        path: 'profile',
        loadComponent: () => import('./songs/pages/profile-page/profile-page'),
      },
      {
        path: '**',
        redirectTo: 'trending'
      }

    ],
  },



  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full',
  },

  {
    path:'**',
    redirectTo: 'dashboard',
  }
];
