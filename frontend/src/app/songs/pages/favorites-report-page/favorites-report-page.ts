import { Component, inject, signal, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../../auth/services/auth.service';
import { CommonModule } from '@angular/common';


@Component({
  selector: 'app-favorites-report-page',
  imports: [CommonModule],
  templateUrl: './favorites-report-page.html',
})
export default class FavoritesReportPage implements OnInit {
  private http = inject(HttpClient);
  private authService = inject(AuthService);

  username = signal<string | null>(null);
  loading = signal(false);
  error = signal<string | null>(null);

  ngOnInit() {
    const user = this.authService.user(); // tomamos el usuario logueado
    this.username.set(user?.username ?? null);
  }

  downloadCSV() {
    const user = this.username();
    if (!user) {
      this.error.set('No hay usuario logueado');
      return;
    }

    this.loading.set(true);
    this.error.set(null);

    this.http.get(`http://localhost:8080/api/usuario/canciones/exportarCSV/${user}/descargar`, {
      responseType: 'blob'
    }).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `favoritas_${user}.csv`;
        a.click();
        window.URL.revokeObjectURL(url);
        this.loading.set(false);
      },
      error: (err) => {
        console.error(err);
        this.error.set('Error descargando archivo');
        this.loading.set(false);
      }
    });
  }
}
