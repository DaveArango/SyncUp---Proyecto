import { Component, inject } from '@angular/core';
import { FavoritesService, Song } from '../../services/favorites.service';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../auth/services/auth.service';

@Component({
  selector: 'app-favorites-report-page',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './favorites-report-page.html',
  styleUrls: ['./favorites-report-page.css']
})
export default class FavoritesReportPage {

  private favoritesService = inject(FavoritesService);
  private authService = inject(AuthService);

  favorites: Song[] = [];
  loading = false;

  constructor() {
    this.loadFavorites();
  }

  private get userId(): string | null {
    return this.authService.user()?.id ?? null;
  }

  loadFavorites() {
    if (!this.userId) return;
    this.loading = true;
    this.favoritesService.getFavorites(this.userId).subscribe(favs => {
      this.favorites = favs;
      this.loading = false;
    });
  }

  // Generar CSV y descargar
  downloadCSV() {
    if (!this.favorites.length) return;

    // Cabeceras
    const headers = ['ID', 'Título', 'Artista', 'Álbum', 'Año'];

    // Contenido
    const rows = this.favorites.map(s => [
      s.id,
      s.titulo,
      s.artista ?? '',
      s.genero ?? '',
      s.anio ?? ''
    ]);

    // Generar CSV como string
    const csvContent =
      [headers, ...rows]
        .map(e => e.map(field => `"${field}"`).join(','))
        .join('\n');

    // Crear Blob y descargar
    const blob = new Blob([csvContent], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);

    const a = document.createElement('a');
    a.href = url;
    a.download = 'mis_canciones_favoritas.csv';
    a.click();
    window.URL.revokeObjectURL(url);
  }
}
