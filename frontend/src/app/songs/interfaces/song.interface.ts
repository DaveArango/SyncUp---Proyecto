export interface Song {
  id: number;
  titulo: string;
  artista: string;
  genero: string;
  anio: number;
  duracion: number;
  audio: string | null;
  rutaArchivo: string;
}

