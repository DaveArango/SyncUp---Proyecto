import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SongService } from '../../services/songs.service';
import { FavoritesService } from '../../services/favorites.service';
import { Song } from '../../interfaces/song.interface';
import { AuthService } from '../../../auth/services/auth.service';

@Component({
  selector: 'app-discovery-page',
  imports: [CommonModule],
  templateUrl: './discovery-page.html',
  styleUrls: ['./discovery-page.css']
})
export default class DiscoveryPage {

  weeklyDiscovery = signal<Song[]>([]);
  loading = signal(true);

  private songService = inject(SongService);
  private favoritesService = inject(FavoritesService);
  private authService = inject(AuthService);

  constructor() {
    this.loadWeeklyDiscovery();
  }

  loadWeeklyDiscovery() {
    this.loading.set(true);
    this.songService.getWeeklyDiscoveryFromAPI().subscribe({
      next: (songsFromAPI: Song[]) => {
        console.log('Weekly discovery recibidas:', songsFromAPI);
        this.weeklyDiscovery.set(songsFromAPI);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error cargando playlist', err);
        this.weeklyDiscovery.set([]);
        this.loading.set(false);
      }
    });
  }

  addToFavorites(song: Song) {
    // AuthService.user() devuelve el usuario directamente (no es función)
    const currentUser = this.authService.user();

    if (!currentUser) {
      console.error('Usuario no autenticado');
      return;
    }

    const username = currentUser.username;
    if (!username) {
      console.error('El usuario no tiene username (requerido por el backend)');
      return;
    }

    this.favoritesService.addFavorite(username, song.id).subscribe({
      next: (ok: boolean) => {
        if (ok) {
          console.log(`${song.titulo ?? song.id} agregado a favoritos`);
        } else {
          console.error('No se pudo agregar a favoritos');
        }
      },
      error: (err) => console.error('Error al agregar favorito:', err)
    });
  }
}
