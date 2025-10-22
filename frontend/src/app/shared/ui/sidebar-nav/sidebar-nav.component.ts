import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-sidebar-nav',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './sidebar-nav.component.html'
})
export class SidebarNavComponent {}
