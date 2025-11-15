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
  selector: 'app-admin-side-menu',
  imports: [RouterLink, CommonModule],
  templateUrl: './admin-side-menu.component.html',
  styleUrls: ['./admin-side-menu.component.css']
})
export class AdminSideMenu {
  menuOptions: MenuOption[] = [
    {
      icon: 'fa-solid fa-chart-line',
      label: 'Dashboard',
      sublabel: 'Visión general',
      route: '/admin/dashboard',
    },
    {
      icon: 'fa-solid fa-plus',
      label: 'Gestionar Canciones',
      sublabel: 'Agregar/Editar/Eliminar canciones',
      route: '/admin/songs',
    },
    {
      icon: 'fa-solid fa-file-csv',
      label: 'Reportes',
      sublabel: 'Exportar CSV',
      route: '/admin/reports',
    }
  ];
}
