import { Component, inject, signal, OnInit } from '@angular/core';
import { SongService } from '../../services/songs.service';
import { Song } from '../../interfaces/song.interface';
import { CommonModule } from '@angular/common';


@Component({
  selector: 'app-radio-page',
  imports:[CommonModule],
  templateUrl: './radio-page.html',
})
export default class RadioPage implements OnInit {

  private songService = inject(SongService);

  radioSongs = signal<Song[]>([]);
  loading = signal(false);
  error = signal<string | null>(null);

  radioId = 8;  // o dinámico si quieres

  ngOnInit() {
    this.loadRadioSongs();
  }

  loadRadioSongs() {
    this.loading.set(true);
    this.error.set(null);

    this.songService.getRadioSongs(this.radioId).subscribe({
      next: (songs) => {
        this.radioSongs.set(songs || []);
        this.loading.set(false);
      },
      error: (err) => {
        console.error(err);
        this.error.set('Error cargando canciones de radio');
        this.loading.set(false);
      }
    });
  }

  audioSrc(song: Song): string {
    return this.songService.getSongAudioUrl(song.id);
  }
}

