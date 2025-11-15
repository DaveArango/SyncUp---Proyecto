import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../../auth/services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-profile-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './profile-page.html',
  styleUrls: ['./profile-page.css'],
})
export default class ProfilePage {
  fb = inject(FormBuilder);
  authService = inject(AuthService);
  router = inject(Router);

  hasError = signal(false);
  success = signal(false);
  isLoading = signal(true);

  user = this.authService.user();

  profileForm = this.fb.group({
    name: ['', [Validators.required]],
    password: ['', [Validators.minLength(6)]],
  });

  constructor() {
    // traer datos del backend
    this.authService.getProfile().subscribe((user) => {
      if (user) {
        this.profileForm.patchValue({ name: user.name });
      }
      this.isLoading.set(false);
    });
  }

  onSubmit() {
    if (this.profileForm.invalid) {
      this.hasError.set(true);
      setTimeout(() => this.hasError.set(false), 2000);
      return;
    }

    const { name, password } = this.profileForm.value;
    this.authService.updateProfile({ name: name || '', password: password || '' })
      .subscribe((updatedUser) => {
        if (updatedUser) {
          this.success.set(true);
          setTimeout(() => this.success.set(false), 2000);
        } else {
          this.hasError.set(true);
          setTimeout(() => this.hasError.set(false), 2000);
        }
      });
  }

  logout() {
    this.authService.logout();
    this.router.navigateByUrl('/auth/login');
  }
}
