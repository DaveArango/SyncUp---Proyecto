import { Song } from "../interfaces/song.interface";

export class SongMapper {

  static mapBackendToSong(cancion: any): Song {
    return {
      id: cancion.id,                      // número, no string
      titulo: cancion.titulo ?? 'Sin título',
      artista: cancion.artista,
      genero: cancion.genero,
      anio: cancion.anio,
      duracion: cancion.duracion,
      audio: cancion.audio,
      rutaArchivo: cancion.rutaArchivo ?? ''
    };
  }

  static mapArray(canciones: any[]): Song[] {
    return canciones.map(c => this.mapBackendToSong(c));
  }

}

