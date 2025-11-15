import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive } from "@angular/router";
import { SongService } from '../../services/songs.service';

interface MenuOption{
  label: string;
  sublabel: string;
  route: string;
  icon: string;
}



@Component({
  selector: 'songs-side-menu-options',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './side-menu-options.html',
})
export class SideMenuOptions {

  songService = inject(SongService)

  menuOptions: MenuOption[] =[
    {
      icon: 'fa-solid fa-chart-line',
      label: 'Trending',
      sublabel: 'Songs Populares',
      route: '/dashboard/trending',
    },
    {
      icon: 'fa-solid fa-magnifying-glass',
      label: 'Buscador',
      sublabel: 'Buscar Songs',
      route: '/dashboard/search',
    },
    {
      icon: 'fa-solid fa-heart',
      label: 'Favoritos',
      sublabel: 'Tus canciones favoritas',
      route: '/dashboard/favorites',
    },
    //  Nueva opción para Descubrimiento Semanal
    {
      icon: 'fa-solid fa-compact-disc',
      label: 'Descubrimiento Semanal',
      sublabel: 'Canciones basadas en tus gustos',
      route: '/dashboard/discovery',
    },
    {
    icon: 'fa-solid fa-broadcast-tower',
    label: 'Radio',
    sublabel: 'Escucha canciones similares',
    route: '/dashboard/radio',
  },
  // Nueva opción para Usuarios sugeridos
    {
      icon: 'fa-solid fa-user-plus',
      label: 'Usuarios Sugeridos',
      sublabel: 'Conectar con otros usuarios',
      route: '/dashboard/users-suggestions',
    },
    //  Nueva opción para Descargar reporte CSV
    {
      icon: 'fa-solid fa-file-csv',
      label: 'Reporte Favoritos',
      sublabel: 'Descargar CSV de tus canciones',
      route: '/dashboard/favorites-report',
    },
  ]
}
