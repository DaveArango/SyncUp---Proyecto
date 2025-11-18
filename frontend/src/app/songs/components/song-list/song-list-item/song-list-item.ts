import { Component, input, inject } from '@angular/core';
import { Song } from '../../../interfaces/song.interface';
import { SongService } from '../../../services/songs.service';
import { AuthService } from '../../../../auth/services/auth.service';

@Component({
  selector: 'song-list-item',
  standalone: true,
  templateUrl: './song-list-item.html',
})
export class SongListItem {
  song = input.required<Song>();

  songService = inject(SongService);
  authService = inject(AuthService);

  addToFavorites(id: number) {
    console.log("CLICK en botón, id =", id);

    const username = this.authService.user()?.username;
    if (!username) return;

    this.songService.addFavorite(username, id).subscribe({
      next: () => console.log("✔️ Agregado a favoritos"),
      error: (err) => console.error("❌ Error:", err)
    });
  }
}
