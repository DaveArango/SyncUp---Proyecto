import { Component, inject, OnInit, computed } from '@angular/core';
import { AuthService } from '../../../auth/services/auth.service';
import { CommonModule } from '@angular/common';
import {UsersService} from '../../services/UsersService';
import { AmigosService  } from '../../services/amigos.service';

@Component({
  selector: 'app-users-suggestions-page',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './users-suggestions-page.html',
  styleUrl: './users-suggestions-page.css'
})
export default class UsersSuggestionsPage implements OnInit {

  private usersService = inject(UsersService);
  private amigosService = inject(AmigosService);
  private authService = inject(AuthService);

  suggestions = this.usersService.suggestions;
  loading = this.usersService.loading;

  ngOnInit() {
    const username = this.authService.user()?.username;
    if (!username) return;

    // Primero cargar amigos
    this.amigosService.getAmigos(username).subscribe(friends => {

      const usernamesFriends = friends.map(f => f.username);

      //  Luego cargar sugerencias
      this.usersService.loadSuggestions(username, usernamesFriends);
    });
  }

  follow(u: any) {
    const currentUsername = this.authService.user()?.username;
    if (!currentUsername || u.isFollowing) return;

    u.isFollowing = true;

    this.usersService.followUser(currentUsername, u.username)
      .subscribe({
        next: () => {
          this.usersService.suggestions.update(prev =>
            prev.filter(s => s.username !== u.username)
          );
        },
        error: () => u.isFollowing = false
      });
  }


  unfollow(user: any) {
    const username = this.authService.user()?.username;
    if (!username) return;

    this.usersService.unfollowUser(username, user.username)
      .subscribe(() => user.isFollowing = false);
  }
}
