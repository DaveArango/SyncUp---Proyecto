import { Component, inject, OnInit } from '@angular/core';
import { UsersService } from '../../../users.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-gestionar-usuarios',
  imports: [CommonModule],
  templateUrl: './gestionar-usuarios.html',
})
export default class GestionarUsuarios implements OnInit {
  usersService = inject(UsersService);

  ngOnInit() {
    this.usersService.fetchAllUsers(); // Cargar usuarios al iniciar
  }

  // getter para acceder a los usuarios (recordar llamar users() en el HTML)
  get users() {
    return this.usersService.allUsers;
  }

  deleteUser(username: string) {
    if (!confirm(`¿Seguro quieres eliminar el usuario ${username}?`)) return;

    this.usersService.deleteUser(username).subscribe({
      next: () => {
        alert(`Usuario ${username} eliminado`);
        this.usersService.fetchAllUsers(); // refrescar lista
      },
      error: err => alert(`Error al eliminar: ${err.message || err}`)
    });
  }
}
