import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminSongService, SongAdmin } from '../../services/admin-song.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-admin-dashboard-page',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-dashboard-page.html',
  styleUrls: ['./admin-dashboard-page.css']
})
export default class AdminDashboardPage {

  private adminService = inject(AdminSongService);

  songs = signal<SongAdmin[]>([]);
  loading = signal(true);

  // Para el formulario de agregar/editar
  formSong = signal<Partial<SongAdmin>>({});

  editingId: string | null = null;

  constructor() {
    this.loadSongs();
  }

  loadSongs() {
    this.loading.set(true);
    this.adminService.getSongs().subscribe(songs => {
      this.songs.set(songs);
      this.loading.set(false);
    });
  }

  startEdit(song: SongAdmin) {
    this.editingId = song.id;
    this.formSong.set({...song});
  }

  cancelEdit() {
    this.editingId = null;
    this.formSong.set({});
  }

  saveSong() {
    const songData = this.formSong();
    if (!songData.title || !songData.artist) return alert('Título y artista son obligatorios');

    if (this.editingId) {
      // Editar
      this.adminService.updateSong(this.editingId, songData).subscribe(updated => {
        if (updated) {
          const updatedList = this.songs().map(s => s.id === updated.id ? updated : s);
          this.songs.set(updatedList);
          this.cancelEdit();
        }
      });
    } else {
      // Agregar
      this.adminService.addSong(songData as Omit<SongAdmin, 'id'>).subscribe(newSong => {
        if (newSong) {
          this.songs.update(list => [...list, newSong]);
          this.formSong.set({});
        }
      });
    }
  }

  deleteSong(songId: string) {
    if (!confirm('¿Seguro que quieres eliminar esta canción?')) return;
    this.adminService.deleteSong(songId).subscribe(ok => {
      if (ok) {
        this.songs.set(this.songs().filter(s => s.id !== songId));
      }
    });
  }
}
