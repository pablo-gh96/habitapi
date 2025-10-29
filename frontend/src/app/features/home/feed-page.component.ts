// src/app/feed/feed-page.component.ts
import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CalendarComponent } from '../../shared/ui/sidebar-nav/calendar-component/calendar-component';
import { UsersService, UserSummary } from '../../services/user.services';
import { from } from 'rxjs';  
import { Router, RouterModule } from '@angular/router';

type UserView = { id: number; name: string; avatarUrl: string };



@Component({
  selector: 'app-feed-page',
  standalone: true,
  imports: [CommonModule, CalendarComponent, RouterModule],
  template: `
  <div class="min-h-screen bg-gray-50">
    <header class="mx-auto max-w-5xl px-4 pt-10 flex items-center justify-between">
  <div>
    <h1 class="text-2xl font-semibold tracking-tight text-gray-900">Calendarios Timeline</h1>
    <p class="text-sm text-gray-500">Todos los usuarios</p>
  </div>

</header>

    <main class="mx-auto max-w-7xl px-4 py-6 space-y-6">
      <app-calendar-component
        *ngFor="let u of users"
        [USER_ID]="u.id"
        [fromProfilePage]="false"
        >
      </app-calendar-component>
    </main>
  </div>
  `
})
export class FeedPageComponent implements OnInit {
  private usersSvc = inject(UsersService);
  username = sessionStorage.getItem('username') || '';
  users: UserView[] = [];
  
  ngOnInit(): void {
    this.usersSvc.getOthers().subscribe({
      next: (list: UserSummary[]) => {
        this.users = list.map(u => ({
          id: u.id,
          name: u.name,
          avatarUrl: `https://api.dicebear.com/9.x/initials/svg?seed=${encodeURIComponent(u.name)}`
        }));
      },
      error: (err) => {
        console.error('❌ Error cargando usuarios:', err);
        this.users = [];
      }
    });
  }
}
