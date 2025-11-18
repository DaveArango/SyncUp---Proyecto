import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SongService } from '../../services/songs.service';
import { Song } from '../../interfaces/song.interface';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-radio-page',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './radio-page.html'
})
export default class RadioPage implements OnInit {

  protected songService = inject(SongService);

  allSongs = signal<Song[]>([]);       // Lista para el combo
  queue = signal<Song[]>([]);          // Cola de reproducción
  currentSong = signal<Song | null>(null);
  loading = signal(false);

  selectedSongId: number | null = null;

  ngOnInit() {
    this.loadAllSongs();
  }

  loadAllSongs() {
    this.songService.getAllSongs().subscribe({
      next: songs => this.allSongs.set(songs),
      error: err => console.error("Error cargando canciones:", err)
    });
  }

  startRadio() {
    if (!this.selectedSongId) return;

    this.loading.set(true);

    this.songService.getSongsByRadio(this.selectedSongId).subscribe({
      next: canciones => {
        this.queue.set(canciones);
        this.currentSong.set(canciones[0]);   // Empieza con la primera
        this.loading.set(false);
      },
      error: err => {
        console.error(err);
        this.loading.set(false);
      }
    });
  }

  onSongEnded() {
    const q = [...this.queue()];
    q.shift(); // eliminar la canción actual

    if (q.length > 0) {
      this.queue.set(q);
      this.currentSong.set(q[0]);
    } else {
      this.currentSong.set(null);
    }
  }
}
