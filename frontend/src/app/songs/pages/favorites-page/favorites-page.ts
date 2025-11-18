import { Component, inject, input, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FavoritesService, Song } from '../../services/favorites.service';
import { AuthService } from '../../../auth/services/auth.service';
import { SongService } from '../../services/songs.service';

@Component({
  selector: 'app-favorites-page',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './favorites-page.html',
  styleUrls: ['./favorites-page.css'],
})
export default class FavoritesPage implements OnInit {

  private favoritesService = inject(FavoritesService);
  private authService = inject(AuthService);
  songService = inject(SongService);

  favorites = signal<Song[]>([]);

  ngOnInit() {
    this.loadFavorites();
  }

  loadFavorites() {
    const currentUser = this.authService.user();

    if (!currentUser || !currentUser.username) {
      console.warn('Usuario no disponible o no tiene username', currentUser);
      this.favorites.set([]);
      return;
    }

    const username = currentUser.username as string;

    this.favoritesService.getFavorites(username).subscribe({
      next: (songs) => {
        console.log('Favoritas recibidas:', songs);
        this.favorites.set(songs);
      },
      error: (err) => {
        console.error('Error cargando favoritas:', err);
        this.favorites.set([]);
      }
    });
  }

  removeFromFavorites(songId: number | string) {
    const currentUser = this.authService.user();
    if (!currentUser || !currentUser.username) {
      console.warn('No se puede eliminar: usuario no válido', currentUser);
      return;
    }

    const username = currentUser.username as string;
    const idNum = typeof songId === 'string' ? Number(songId) : songId;

    if (Number.isNaN(idNum)) {
      console.warn('songId inválido:', songId);
      return;
    }

    this.favoritesService.removeFavorite(username, idNum).subscribe({
      next: (ok) => {
        if (ok) {
          this.favorites.set(this.favorites().filter((s: Song) => s.id !== idNum));
        } else {
          console.warn('El backend indicó que no pudo eliminar la favorita.');
        }
      },
      error: (err) => console.error('Error eliminando favorito:', err)
    });
  }
}

