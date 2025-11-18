import { Component, inject, signal } from '@angular/core';
import { SongService } from '../../services/songs.service';
import { Song } from '../../interfaces/song.interface';
import { CommonModule } from '@angular/common';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-busqueda-avanzada-page',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './busqueda-avanzada-page.html',
  styleUrl: 'busqueda-avanzada-page.css'
})
export default class BusquedaAvanzadaPage {

  protected songService = inject(SongService);

  artista = signal('');
  genero = signal('');
  anio = signal<number | null>(null);
  esAnd = signal(true);

  resultados = signal<Song[]>([]);
  buscando = signal(false);

  buscar() {
    this.buscando.set(true);

    this.songService.advancedSearch(
      this.artista(),
      this.genero(),
      this.anio(),
      this.esAnd()
    )
      .subscribe({
        next: (resp) => {
          this.resultados.set(resp);
          this.buscando.set(false);
        },
        error: () => {
          this.resultados.set([]);
          this.buscando.set(false);
        }
      });
  }
}

