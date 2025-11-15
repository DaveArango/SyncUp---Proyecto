import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule ,Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register-page',
  imports: [CommonModule,RouterLink, ReactiveFormsModule],
  templateUrl: './register-page.html',
  styleUrls: ['./register-page.css']
})
export class RegisterPage {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  registerForm: FormGroup = this.fb.group({
    name: ['', [Validators.required, Validators.minLength(3)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
  });

  hasError = false;

  onSubmit() {
    if (this.registerForm.invalid) {
      this.hasError = true;
      return;
    }

    const { name, email, password } = this.registerForm.value;

    // Aquí llamas al servicio que hace el registro en el backend
    this.authService.register(name, email, password).subscribe((ok) => {
      if (ok) {
        this.router.navigateByUrl('/auth/login');
      } else {
        this.hasError = true;
      }
    });
  }
 }
