import { Component, input, inject } from '@angular/core';
import { Song } from '../../../interfaces/song.interface';
import {SongService} from '../../../services/songs.service';

@Component({
  selector: 'song-list-item',
  templateUrl: './song-list-item.html',
})
export class SongListItem {
  song = input.required<Song>();
  songService = inject(SongService);
  protected readonly Number = Number;

}
