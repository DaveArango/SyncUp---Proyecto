import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login-page',
  imports: [CommonModule, RouterLink, ReactiveFormsModule],
  standalone: true,
  templateUrl: './login-page.html',
  styleUrls: ['./login-page.css']
})
export class LoginPage {
  fb = inject(FormBuilder);
  hasError = signal(false);
  errorMessage = signal('');
  isPosting = signal(false);
  router = inject(Router);
  authService = inject(AuthService);

  // Cambiado de "email" a "username" y quitada la validación de email
  loginForm = this.fb.group({
    username: ['', [Validators.required, Validators.minLength(3)]],
    password: ['', [Validators.required, Validators.minLength(6)]],
  });

  onSubmit() {
    if (this.loginForm.invalid) {
      this.hasError.set(true);
      this.errorMessage.set('Por favor complete correctamente los campos.');
      setTimeout(() => this.hasError.set(false), 2000);
      return;
    }

    const { username = '', password = '' } = this.loginForm.value;

    this.isPosting.set(true);
    this.authService.login(username!, password!).subscribe({
      next: () => {
        this.isPosting.set(false);
        this.hasError.set(false);
        this.router.navigateByUrl('/dashboard');
      },
      error: (err) => {
        this.isPosting.set(false);
        this.hasError.set(true);
        this.errorMessage.set(err?.error?.error || 'Usuario o contraseña incorrectos');
        setTimeout(() => this.hasError.set(false), 3000);
      }
    });
  }
}
