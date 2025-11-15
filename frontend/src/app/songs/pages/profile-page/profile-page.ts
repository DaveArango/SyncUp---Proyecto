import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../../auth/services/auth.service';

@Component({
  selector: 'app-profile-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './profile-page.html',
  styleUrls: ['./profile-page.css']
})
export default class ProfilePage {
  fb = inject(FormBuilder);
  authService = inject(AuthService);
  router = inject(Router);

  isLoading = signal(true);
  hasError = signal(false);
  success = signal(false);

  profileForm = this.fb.group({
    name: ['', [Validators.required, Validators.minLength(3)]],
    password: ['', [Validators.minLength(6)]]
  });

  constructor() {
    this.authService.getProfile().subscribe(user => {
      if (user) this.profileForm.patchValue({ name: user.name });
      this.isLoading.set(false);
    });
  }

  onSubmit() {
    if (this.profileForm.invalid) {
      this.hasError.set(true);
      setTimeout(() => this.hasError.set(false), 2000);
      return;
    }

    const { name, password } = this.profileForm.value as { name: string; password: string };
    const updateData: { name: string; password?: string } = { name };
    if (password && password.trim().length > 0) updateData.password = password;

    this.authService.updateProfile(updateData).subscribe(updatedUser => {
      if (updatedUser) {
        this.success.set(true);
        this.profileForm.patchValue({ name: updatedUser.name, password: '' });
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
