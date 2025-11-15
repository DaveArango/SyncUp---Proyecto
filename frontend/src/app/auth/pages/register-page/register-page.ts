import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register-page',
  imports: [CommonModule, RouterLink, ReactiveFormsModule],
  templateUrl: './register-page.html',
  styleUrls: ['./register-page.css']
})
export class RegisterPage {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  registerForm: FormGroup = this.fb.group({
    username: ['', [Validators.required, Validators.minLength(3)]],
    nombre: ['', [Validators.required, Validators.minLength(3)]],
    password: ['', [Validators.required, Validators.minLength(6)]],
  });

  hasError = false;
  errorMessage = '';

  onSubmit() {
    if (this.registerForm.invalid) {
      this.hasError = true;
      this.errorMessage = 'Por favor complete correctamente todos los campos.';
      return;
    }

    const { username, nombre, password } = this.registerForm.value;

    this.authService.register(username, nombre, password).subscribe({
      next: () => {
        this.hasError = false;
        this.router.navigateByUrl('/auth/login');
      },
      error: (err) => {
        this.hasError = true;
        this.errorMessage = err?.error?.error || 'Por favor revise la información ingresada.';
      }
    });
  }
}
