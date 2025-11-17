import { Component, inject, signal, OnInit } from '@angular/core';

import { FavoritesService, Song } from '../../services/favorites.service';
import { SongService } from '../../services/songs.service';
import { GrafoService, Cancion } from '../../services/grafo.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-discovery-page',
  imports: [CommonModule],
  templateUrl: './discovery-page.html',
})
export default class DiscoveryPage implements OnInit {

  private songService = inject(SongService);
  private favService = inject(FavoritesService);
  private grafoService = inject(GrafoService);

  similarSongs = signal<Cancion[]>([]);
  loading = signal(false);
  error = signal<string | null>(null);

  strategy = signal<'user' | 'current' | 'fixed'>('user');

  defaultId = 5;

  // Obtener ID de la canción actual
  get currentSongId(): number | null {
    try {
      const cur = (this.songService as any).currentSong?.();
      return cur ? Number(cur.id) : null;
    } catch {
      return null;
    }
  }

  ngOnInit() {
    console.log(">>> ngOnInit ejecutado");
    this.loadForStrategy();
  }

  async loadForStrategy() {
    console.log(">>> loadForStrategy ejecutado");

    this.loading.set(true);
    this.error.set(null);

    let idToUse: number | null = null;
    const strat = this.strategy();

    try {
      // --- Estrategia USER ---
      if (strat === 'user') {
        const username = localStorage.getItem('username');
        if (username) {
          const favs = await this.favService.getFavorites(username).toPromise();
          if (favs && favs.length > 0) {
            idToUse = Number(favs[0].id);
          }
        }
      }

      // --- Estrategia CURRENT ---
      if (!idToUse && (strat === 'user' || strat === 'current')) {
        const curId = this.currentSongId;
        if (curId) idToUse = curId;
      }

      // --- Estrategia FIXED ---
      if (!idToUse) idToUse = this.defaultId;

      console.log(">>> ID utilizado:", idToUse);

      // --- Llamar al endpoint de similares ---
      this.grafoService.obtenerSimilares(idToUse).subscribe({
        next: (songs) => {
          console.log(">>> Canciones similares recibidas:", songs);

          this.similarSongs.set(songs || []);
          this.loading.set(false);
        },
        error: (err) => {
          console.error(">>> ERROR obteniendo similares:", err);
          this.similarSongs.set([]);
          this.error.set('Error cargando recomendaciones');
          this.loading.set(false);
        }
      });

    } catch (err) {
      console.error(">>> ERROR inesperado en loadForStrategy:", err);
      this.error.set('Error inesperado');
      this.loading.set(false);
    }
  }

  onChangeStrategy(s: 'user' | 'current' | 'fixed') {
    this.strategy.set(s);
    this.loadForStrategy();
  }
  audioSrc(song: Cancion): string {
  return this.songService.getSongAudioUrl(song.id);
}

}
