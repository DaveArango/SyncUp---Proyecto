import { Component, input, inject } from '@angular/core';
import { Song } from '../../../interfaces/song.interface';
import { SongService } from '../../../services/songs.service';
import { AuthService } from '../../../../auth/services/auth.service';
import { FavoritesService } from '../../../services/favorites.service';
@Component({
  selector: 'song-list-item',
  standalone: true,
  templateUrl: './song-list-item.html',
})
export class SongListItem {
  song = input.required<Song>();

  songService = inject(SongService);
  authService = inject(AuthService);
  favoritesService = inject(FavoritesService);

  addToFavorites(id: number) {
    const username = this.authService.user()?.username;
    if (!username) return;

    // ❗ Si ya está en favoritos, NO volver a agregar
    if (this.favoritesService.favoritesIds().includes(id)) {
      console.warn("❗ Canción ya está en favoritos, no se agregará otra vez");
      return;
    }

    this.songService.addFavorite(username, id).subscribe({
      next: () => {
        console.log("✔️ Agregado a favoritos");

        // Añadir al signal global
        const current = this.favoritesService.favoritesIds();
        this.favoritesService.favoritesIds.set([...current, id]);
      },
      error: (err) => console.error("❌ Error:", err)
    });
  }

}
