import { Song } from "../interfaces/song.interface";

export class SongMapper {
  static mapBackendToSong(cancion: any): Song {
    return {
      id: cancion.id.toString(),
      title: cancion.titulo ?? 'Sin título',
      url: cancion.rutaArchivo ?? '',
      artista: cancion.artista,
      genero: cancion.genero,
      anio: cancion.anio,
      duracion: cancion.duracion,
      audio: cancion.audio
    };
  }

  static mapArray(canciones: any[]): Song[] {
    return canciones.map(c => this.mapBackendToSong(c));
  }
}

