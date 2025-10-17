import { Injectable } from '@angular/core';
import { CanActivate, Router, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(private router: Router) {}

  canActivate(): boolean | UrlTree | Observable<boolean | UrlTree> | Promise<boolean | UrlTree> {
    const token = sessionStorage.getItem('token'); // o sessionStorage si prefieres
    if (token) {
      return true;
    } else {
      // Redirige al login si no hay token
      return this.router.createUrlTree(['/login']);
    }
  }
}
