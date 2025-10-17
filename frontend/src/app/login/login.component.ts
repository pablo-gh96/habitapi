import { Component, inject, signal } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../services/auth.service';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: 'login.component.html',
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);
  
  loading = signal(false);
  serverMsg = signal<{ type: 'ok' | 'error'; text: string } | null>(null);

  form = this.fb.group({
    username: ['', [Validators.required, Validators.minLength(3)]],
    password: ['', [Validators.required, Validators.minLength(3)]],
  });

  submit() {
    this.serverMsg.set(null);
    if (this.form.invalid) return;
    this.loading.set(true);

    const { username, password } = this.form.getRawValue()!;
    this.auth.login(username ?? '', password ?? '').subscribe({
      next: (res) => {
        const myUsername = sessionStorage.getItem('username');
        this.router.navigate(['/profile']);
      },
      error: (err) => {
        const text = err?.error?.error ?? 'Credenciales inválidas';
        this.serverMsg.set({ type: 'error', text });
      },
      complete: () => this.loading.set(false),
    });
  }
}
