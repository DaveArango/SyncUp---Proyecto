import { Component, OnInit, inject, signal } from '@angular/core';
import { Song } from '../../../../songs/interfaces/song.interface';
import { SongsService } from '../song.service';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';


@Component({
  selector: 'app-songs-list',
  imports: [RouterLink, CommonModule],
  standalone: true,
  templateUrl: './songs-list.component.html'
})
export default class SongsListComponent implements OnInit {
  private songsService = inject(SongsService);
  songs = signal<Song[]>([]);

  ngOnInit() {
    this.loadSongs();
  }

  loadSongs() {
    this.songsService.getAll().subscribe(songs => this.songs.set(songs));
  }

  deleteSong(id: string) {
    if (!confirm('¿Seguro que quieres eliminar esta canción?')) return;

    this.songsService.delete(id).subscribe(success => {
      if (success) this.loadSongs();
    });
  }

  protected readonly String = String;
}
