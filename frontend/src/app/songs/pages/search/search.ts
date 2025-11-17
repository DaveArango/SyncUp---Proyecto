import { Component, inject, signal } from '@angular/core';
import { SongList } from "../../components/song-list/song-list";
import { SongService } from '../../services/songs.service';
import { Song } from '../../interfaces/song.interface';
import { CommonModule } from '@angular/common';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-search',
  imports: [SongList, CommonModule, FormsModule],
  templateUrl: './search.html',
  standalone: true,
  styleUrl: './search.css'
})
export default class Search {
  songService = inject(SongService);

  songs = signal<Song[]>([]);
  suggestions = signal<string[]>([]);
  searchQuery = signal<string>('');
  currentAudio = signal<string | null>(null);

  // Cuando escribes
  onInputChange(query: string) {
    this.searchQuery.set(query);

    if (!query) {
      this.suggestions.set([]);
      return;
    }

    this.songService.searchSuggestions(query).subscribe({
      next: res => this.suggestions.set(res),
      error: err => console.error(err)
    });
  }

  // Cuando presionas Enter o seleccionas sugerencia
  onSearch(query: string) {
    if (!query) return;

    this.searchQuery.set(query);
    this.suggestions.set([]);

    // Buscar las canciones completas
    this.songService.searchSongsByName(query).subscribe({
      next: res => {
        this.songs.set(res);

        if (res.length > 0) {
          const url = this.songService.getSongAudioUrl(Number(res[0].id));
          this.currentAudio.set(url);

          setTimeout(() => {
            const audioEl = document.getElementById('mainAudio') as HTMLAudioElement;
            if (audioEl) audioEl.play().catch(err => console.error(err));
          }, 100);
        } else {
          this.currentAudio.set(null);
        }
      },
      error: err => console.error(err)
    });
  }



  // Seleccionar sugerencia
  selectSuggestion(suggestion: string) {
    this.searchQuery.set(suggestion);
    this.onSearch(suggestion);
  }
}
