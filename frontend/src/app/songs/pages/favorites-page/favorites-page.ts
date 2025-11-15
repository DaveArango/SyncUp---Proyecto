import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SongService } from '../../services/songs.service';
import { Song } from '../../interfaces/song.interface';
import { AuthService } from '../../../auth/services/auth.service';

@Component({
  selector: 'app-favorites-page',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './favorites-page.html',
  styleUrls: ['./favorites-page.css'],
})
export default class FavoritesPage implements OnInit {

  private songService = inject(SongService); // <-- aquí inyectas
  private authService = inject(AuthService); // <-- necesitas para el usuario

  favorites = signal<Song[]>([]);

  ngOnInit() {
    this.loadFavorites();
  }

  loadFavorites() {
    const currentUser = this.authService.user();
    if (!currentUser) {
      this.favorites.set([]);
      return;
    }

    this.songService.getFavorites(currentUser.email).subscribe({
      next: (songs) => this.favorites.set(songs),
      error: (err) => {
        console.error('Error cargando favoritos:', err);
        this.favorites.set([]);
      }
    });
  }

  removeFromFavorites(songId: string) {
    const currentUser = this.authService.user();
    if (!currentUser || !currentUser.email) return;

    this.songService.removeFavorite(currentUser.email, Number(songId)).subscribe({
      next: () => {
        // filtramos usando s.id de tipo string
        this.favorites.set(this.favorites().filter((s: Song) => s.id !== songId));
      },
      error: (err) => console.error('Error eliminando favorito:', err)
    });
  }

}

