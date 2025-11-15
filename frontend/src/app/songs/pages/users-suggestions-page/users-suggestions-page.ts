import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UsersService, UserSuggestion } from '../../../../environments/users.service';
import { AuthService } from '../../../auth/services/auth.service';


@Component({
  selector: 'app-users-suggestions-page',
  imports: [CommonModule],
  templateUrl: './users-suggestions-page.html',
  styleUrls: ['./users-suggestions-page.css']
})
export default class UsersSuggestionsPage {

  private usersService = inject(UsersService);
  private authService = inject(AuthService);

  suggestions = this.usersService.suggestions;
  loading = this.usersService.loading;

  constructor() {
    this.loadSuggestions();
  }

  private get userId(): string | null {
    return this.authService.user()?.id ?? null;
  }

  loadSuggestions() {
    if (!this.userId) return;
    this.usersService.loadSuggestions(this.userId);
  }

  toggleFollow(user: UserSuggestion) {
    if (!this.userId) return;

    if (user.isFollowing) {
      this.usersService.unfollowUser(this.userId, user.id).subscribe(ok => {
        if (ok) user.isFollowing = false;
      });
    } else {
      this.usersService.followUser(this.userId, user.id).subscribe(ok => {
        if (ok) user.isFollowing = true;
      });
    }
  }
}
