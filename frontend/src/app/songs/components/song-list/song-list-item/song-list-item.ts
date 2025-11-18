import { Component, input, inject, signal } from '@angular/core';
import { Song } from '../../../interfaces/song.interface';
import { SongService } from '../../../services/songs.service';
import { AuthService } from '../../../../auth/services/auth.service';
import { FavoritesService } from '../../../services/favorites.service';
import {NgClass} from '@angular/common';

@Component({
  selector: 'song-list-item',
  standalone: true,
  templateUrl: './song-list-item.html',
  imports: [
    NgClass
  ]
})
export class SongListItem {
  song = input.required<Song>();

  songService = inject(SongService);
  authService = inject(AuthService);
  favoritesService = inject(FavoritesService);

  // ⭐ Estado local del botón
  isFavorite = signal(false);

  addToFavorites(id: number) {

    const username = this.authService.user()?.username;
    if (!username) return;

    // Si ya está en favoritos → marcamos el botón también
    if (this.favoritesService.favoritesIds().includes(id)) {
      this.isFavorite.set(true);
      return;
    }

    this.songService.addFavorite(username, id).subscribe({
      next: () => {
        console.log("✔️ Agregado a favoritos");

        // Actualizar el global
        const current = this.favoritesService.favoritesIds();
        this.favoritesService.favoritesIds.set([...current, id]);

        // ⭐ Cambiar botón a "Favorito"
        this.isFavorite.set(true);
      },
      error: (err) => console.error("❌ Error:", err)
    });
  }
}
