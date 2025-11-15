import { Component, inject, OnInit } from '@angular/core';
import { SongList } from "../../components/song-list/song-list";
import { SongService } from '../../services/songs.service';

@Component({
  selector: 'app-trending-page',
  imports: [SongList],
  templateUrl: './trending-page.html',
})
export default class TrendingPage implements OnInit {
  songService = inject(SongService);

  ngOnInit() {
  }
}
