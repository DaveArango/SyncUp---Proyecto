import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

interface MenuOption{
  label: string;
  sublabel: string;
  route: string;
  icon: string;
}

@Component({
  selector: 'admin-side-menu-options',
  standalone: true,
  imports: [RouterLink, CommonModule],
  templateUrl: './side-menu-options.html',
  styleUrls: ['./side-menu-options.css'],
})
export default class SideMenuOptions {

  menuOptions: MenuOption[] =[
    {
      icon: 'fa-solid fa-chart-line',
      label: 'Cargar Canciones',
      sublabel: 'masivo',
      route: '/admin/dashboard-admin/cargar-canciones',
    },
    {
      icon: 'bi bi-people-fill',
      label: 'Gestionar Canciones',
      sublabel: 'catalogo',
      route: '/admin/dashboard-admin/gestionar-canciones',
    },
    {
      icon: 'fa-solid fa-magnifying-glass',
      label: 'Gestionar usuarios',
      sublabel: 'listar',
      route: '/admin/dashboard-admin/gestionar-usuarios',
    },
  ]
 }
