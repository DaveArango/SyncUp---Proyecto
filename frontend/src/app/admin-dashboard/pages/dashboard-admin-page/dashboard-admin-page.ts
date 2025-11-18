import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import SideMenuHeader from '../../componentes/side-menu-header/side-menu-header/side-menu-header';
import SideMenuOptions from '../../componentes/side-menu-options/side-menu-options/side-menu-options';

@Component({
  selector: 'app-dashboard-admin-page',
  standalone: true,
  imports: [RouterOutlet, SideMenuHeader, SideMenuOptions],
  templateUrl: './dashboard-admin-page.html',
  styleUrls: ['./dashboard-admin-page.css'],
})
export default class DashboardAdminPage {}

