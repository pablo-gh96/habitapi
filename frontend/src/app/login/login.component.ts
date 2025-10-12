import { Component, inject, signal } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../services/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: 'login.component.html',
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);

  loading = signal(false);
  serverMsg = signal<{ type: 'ok' | 'error'; text: string } | null>(null);

  form = this.fb.group({
    name: ['', [Validators.required, Validators.minLength(3)]],
    password: ['', [Validators.required, Validators.minLength(3)]],
  });

  submit() {
    this.serverMsg.set(null);
    if (this.form.invalid) return;
    this.loading.set(true);

    const { name, password } = this.form.getRawValue()!;
    this.auth.login({
      name: name ?? '',
      password: password ?? ''
    }).subscribe({
      next: (res) => {
        this.serverMsg.set({ type: 'ok', text: res?.message ?? 'Login correcto' });
        // Aquí puedes navegar a /perfil o recargar calendario
        // this.router.navigate(['/perfil']);
      },
      error: (err) => {
        const text = err?.error?.error ?? 'Credenciales inválidas';
        this.serverMsg.set({ type: 'error', text });
      },
      complete: () => this.loading.set(false),
    });
  }
}
