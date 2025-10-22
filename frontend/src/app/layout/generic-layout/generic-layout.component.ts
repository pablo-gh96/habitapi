import { Component , inject} from '@angular/core';
import { SidebarNavComponent } from '../../shared/ui/sidebar-nav/sidebar-nav.component';
import { RouterOutlet } from '@angular/router';
import { RouterModule, Router } from '@angular/router';

@Component({
  selector: 'app-generic-layout',
  imports: [RouterOutlet,SidebarNavComponent, RouterModule],
  templateUrl: './generic-layout.component.html'
})
export class GenericLayoutComponent {

    constructor() {}
    private router = inject(Router);
    logout(): void {
    // Borra el token del almacenamiento local
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    sessionStorage.clear();
    
    
    // Redirige al login
    this.router.navigate(['/login']);
  }
}
