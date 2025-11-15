import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SongService } from '../../services/songs.service';
import { FavoritesService, Song } from '../../services/favorites.service';
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
    this.songService.getWeeklyDiscoveryFromAPI().subscribe({
      next: (songsFromAPI: Song[]) => {
        // Los campos extras son opcionales, podemos usar directamente la respuesta
        this.weeklyDiscovery.set(songsFromAPI);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error cargando playlist', err);
        this.loading.set(false);
      }
    });
  }

  addToFavorites(song: Song) {
    // Tomamos el usuario autenticado desde AuthService
    const currentUser = this.authService.user(); // computed<User | null>
    if (!currentUser) {
      console.error('Usuario no autenticado');
      return;
    }

    const userId = currentUser.id;

    this.favoritesService.addFavorite(userId, song.id).subscribe(ok => {
      if (ok) {
        console.log(`${song.title} agregado a favoritos`);
      } else {
        console.error('No se pudo agregar a favoritos');
      }
    });
  }
}
