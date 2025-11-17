import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RadioService } from '../../services/radio.service';
import { FavoritesService, Song } from '../../services/favorites.service';
import { AuthService } from '../../../auth/services/auth.service';

@Component({
  selector: 'app-radio-page',
  imports: [CommonModule],
  templateUrl: './radio-page.html',
  styleUrls: ['./radio-page.css']
})
export default class RadioPage {

  queue = signal<Song[]>([]);
  loading = signal(false);
  currentSong = signal<Song | null>(null);
  isPlaying = signal(false);

  private radioService = inject(RadioService);
  private favoritesService = inject(FavoritesService);
  private authService = inject(AuthService);

  private audio = new Audio();

  constructor() {
    // Escuchar cuando termina la canción para pasar a la siguiente
    this.audio.addEventListener('ended', () => this.playNextSong());
  }

  startRadio(songId: string) {
    this.loading.set(true);
    this.radioService.startRadioFromSong(songId).subscribe({
      next: (songs) => {
        this.queue.set(songs);
        this.loading.set(false);
        if (songs.length > 0) this.playSong(songs[0]);
      },
      error: (err) => {
        console.error('Error cargando radio', err);
        this.loading.set(false);
      }
    });
  }

  playSong(song: Song) {
    if (!song.rutaArchivo) {
      console.error('La canción no tiene URL de reproducción');
      return;
    }

    this.currentSong.set(song);
    this.audio.src = song.rutaArchivo;
    this.audio.play().then(() => this.isPlaying.set(true)).catch(err => console.error(err));
  }

  togglePlayPause() {
    if (!this.currentSong()) return;
    if (this.isPlaying()) {
      this.audio.pause();
      this.isPlaying.set(false);
    } else {
      this.audio.play().then(() => this.isPlaying.set(true)).catch(err => console.error(err));
    }
  }

  playNextSong() {
    const queue = this.queue();
    const current = this.currentSong();
    if (!current) return;

    const currentIndex = queue.findIndex(s => s.id === current.id);
    const nextIndex = currentIndex + 1;
    if (nextIndex < queue.length) {
      this.playSong(queue[nextIndex]);
    } else {
      // Fin de la cola
      this.isPlaying.set(false);
      this.currentSong.set(null);
    }
  }

  addToFavorites(song: Song) {
    const currentUser = this.authService.user();
    if (!currentUser) {
      console.error('Usuario no autenticado');
      return;
    }

    const userId = currentUser.id;

    this.favoritesService.addFavorite(userId, song.id).subscribe(ok => {
      if (ok) console.log(`${song.titulo} agregado a favoritos`);
      else console.error('No se pudo agregar a favoritos');
    });
  }
}
