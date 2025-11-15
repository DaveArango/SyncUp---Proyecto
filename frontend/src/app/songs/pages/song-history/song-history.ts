import { Component,computed,inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute } from '@angular/router';
import { map } from 'rxjs';
import { SongService } from '../../services/songs.service';
import { SongList } from "../../components/song-list/song-list";

@Component({
  selector: 'app-song-history',
  imports: [SongList],
  templateUrl: './song-history.html',
})
export default class SongHistory {

  songService = inject(SongService)
  query = toSignal(
    inject(ActivatedRoute).params.pipe(
      map( params => params['query'])
    )
  );
  songsByKey = computed(() => this.songService.getHistorySongs(this.query()));
}

