import { Component, input } from '@angular/core';
import { SongListItem } from "./song-list-item/song-list-item";
import { Song } from '../../interfaces/song.interface';

@Component({
  selector: 'song-list',
  imports: [SongListItem],
  templateUrl: './song-list.html',
})
export class SongList {
  songs = input.required<Song[]>();
}

