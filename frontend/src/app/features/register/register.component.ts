import { Component, inject, signal } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './register.component.html',
})
export class RegisterComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);

  serverMsg = signal<{ type: 'ok' | 'error'; text: string } | null>(null);

  form = this.fb.group({
    name: ['', [Validators.required, Validators.minLength(3)]],
    password: ['', [Validators.required, Validators.minLength(3)]],
    confirm: ['', [Validators.required, Validators.minLength(3)]],
    username: ['', [Validators.required, Validators.minLength(3)]],
    lastname: ['', [Validators.required, Validators.minLength(3)]],
    email: ['', [Validators.required, Validators.email]]
  });

  submit() {
    this.serverMsg.set(null);
    if (this.form.invalid) return;

    const {username, password, confirm, name, lastname, email } = this.form.getRawValue()!;
    if (password !== confirm) {
      this.serverMsg.set({ type: 'error', text: 'Las contraseñas no coinciden.' });
      return;
    }

    // Ensure name and password are strings (not null)
    if (typeof name !== 'string' || typeof password !== 'string' || typeof username !== 'string' || typeof lastname !== 'string' || typeof email !== 'string') {
      this.serverMsg.set({ type: 'error', text: 'Nombre y contraseña son requeridos.' });
      return;
    }

    this.auth.register({ username, password, name, lastname, email }).subscribe({
      next: (res) => {
        const text = res?.message ?? 'Usuario creado correctamente';
        this.serverMsg.set({ type: 'ok', text });
      },
      error: (err) => {
        const text = err?.error?.error ?? 'No se pudo crear el usuario';
        this.serverMsg.set({ type: 'error', text });
      },
    });
  }
}
