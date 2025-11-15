import { Component, inject, computed } from '@angular/core';
import { environment } from '../../../../environments/environment.development';
import { AuthService } from '../../../auth/services/auth.service';
import { RouterLink } from '@angular/router';
import { User } from '../../../auth/interfaces/user.interface';

@Component({
  selector: 'songs-side-menu-header',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './side-menu-header.html',
})
export class SideMenuHeader {
  envs = environment;
  private authService = inject(AuthService);

  // Computed para que se actualice automáticamente cuando cambie el usuario
  userName = computed(() => this.authService.user()?.name || 'Usuario');
}
