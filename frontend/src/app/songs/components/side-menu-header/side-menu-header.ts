import { Component, inject } from '@angular/core';
import { environment } from '../../../../environments/environment.development';
import { AuthService } from '../../../auth/services/auth.service'; // 👈 importa tu servicio
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'songs-side-menu-header',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './side-menu-header.html',
})
export class SideMenuHeader {
  envs = environment;
  authService = inject(AuthService);

  get userName() {
    return this.authService.user()?.name || 'Usuario';
  }
}
