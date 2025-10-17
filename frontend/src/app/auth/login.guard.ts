import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class LoginGuard implements CanActivate {
  constructor(private router: Router) {}

  canActivate(): boolean {
    const token = sessionStorage.getItem('token');

    if (token) {
      const myUsername = sessionStorage.getItem('username');
      this.router.navigate(['/', myUsername]);
      return false;
    }
    return true;
  }
}