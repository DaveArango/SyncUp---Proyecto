import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AmigosService, Amigo } from '../../services/amigos.service';
import { AuthService } from '../../../auth/services/auth.service';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-amigos-page',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './amigos-page.html',
  styleUrl: './amigos-page.css'
})
export default class AmigosPage {

  amigosService = inject(AmigosService);
  authService = inject(AuthService);
  http = inject(HttpClient);

  amigos = signal<Amigo[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);

  currentUserUsername: string | null = null;

  constructor() {

    const currentUser = this.authService.user();

    if (!currentUser) {
      this.error.set('No hay usuario autenticado.');
      this.loading.set(false);
      return;
    }

    this.currentUserUsername = currentUser.username;

    this.amigosService.getAmigos(this.currentUserUsername).subscribe({
      next: (data) => {
        this.amigos.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Error obteniendo amigos.');
        this.loading.set(false);
      }
    });
  }

  /** ------------------------------------------------------
   *  Dejar de seguir a un usuario
   *  POST: /api/usuario/{username}/dejarDeSeguir/{seguido}
   * ------------------------------------------------------*/
  dejarDeSeguir(seguidoUsername: string) {
    if (!this.currentUserUsername) return;

    const url = `http://localhost:8080/api/usuario/${this.currentUserUsername}/dejarDeSeguir/${seguidoUsername}`;

    this.http.post(url, {}).subscribe({
      next: () => {
        // Actualiza la lista quitando al amigo
        this.amigos.update(amigos =>
          amigos.filter(a => a.username !== seguidoUsername)
        );
      },
      error: (err) => {
        console.error(err);
        this.error.set('No se pudo dejar de seguir.');
      }
    });
  }
}
