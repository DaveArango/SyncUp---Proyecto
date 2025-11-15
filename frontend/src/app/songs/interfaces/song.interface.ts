export interface Song {
  id: string;
  title: string;
  url: string;

  // campos adicionales del backend
  artista?: string;
  genero?: string;
  anio?: number;
  duracion?: number;
  audio?: string;
}

