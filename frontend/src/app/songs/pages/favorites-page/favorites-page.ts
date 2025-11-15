import { Component, inject, OnInit, signal } from '@angular/core';
import { FavoritesService, Song } from '../../services/favorites.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-favorites-page',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './favorites-page.html',
  styleUrls: ['./favorites-page.css'],
})
export default class FavoritesPage implements OnInit {
  private favoritesService = inject(FavoritesService);
  favorites = signal<Song[]>([]);
  userId = '';

  ngOnInit() {
    this.loadFavorites();
  }

  loadFavorites() {
    this.favoritesService.getFavorites(this.userId).subscribe((songs) => {
      this.favorites.set(songs);
    });
  }

  removeFromFavorites(songId: string) {
    this.favoritesService.removeFavorite(this.userId, songId).subscribe((ok) => {
      if (ok) {
        this.favorites.set(this.favorites().filter((s) => s.id !== songId));
      }
    });
  }
}
