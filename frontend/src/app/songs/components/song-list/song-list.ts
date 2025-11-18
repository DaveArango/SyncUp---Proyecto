import { Component, input } from '@angular/core';
import { Song } from '../../interfaces/song.interface';
import { SongListItem } from './song-list-item/song-list-item';

@Component({
  selector: 'song-list',
  standalone: true,
  imports: [SongListItem],
  templateUrl: './song-list.html',
})
export class SongList {
  songs = input.required<Song[]>();
}
