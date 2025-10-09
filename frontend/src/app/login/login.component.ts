import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: 'login.component.html'
})
export class LoginComponent {
  username = '';
  password = '';
  message = '';
  error = '';

  constructor(private auth: AuthService) {}

  submit() {
    this.error = '';
    this.auth.login(this.username, this.password).subscribe({
      next: () => this.message = 'Login correcto ✅ ' + localStorage.getItem('token'),
      error: () => this.error = 'Credenciales inválidas ❌'
    });
  }

  checkUser() {
    this.auth.me().subscribe({
      next: res => this.message = `Usuario actual: ${res.username}`,
      error: () => this.error = 'Token inválido o expirado'
    });
  }
}
