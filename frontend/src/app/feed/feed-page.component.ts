// src/app/feed/feed-page.component.ts
import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PublicCalendarComponent } from './public-calendar.component';
import { UsersService, UserSummary } from '../services/user.services';
import { from } from 'rxjs';

type UserView = { id: number; name: string; avatarUrl: string };

// TODO: sustituir por el id real del usuario autenticado
const MY_ID = 1;

@Component({
  selector: 'app-feed-page',
  standalone: true,
  imports: [CommonModule, PublicCalendarComponent],
  template: `
  <div class="min-h-screen bg-gray-50">
    <header class="mx-auto max-w-5xl px-4 pt-10">
      <h1 class="text-2xl font-semibold tracking-tight text-gray-900">Calendarios (feed)</h1>
      <p class="text-sm text-gray-500">Todos los usuarios</p>
    </header>

    <main class="mx-auto max-w-5xl px-4 py-6 space-y-6">
      <app-public-calendar
        *ngFor="let u of users"
        [userId]="u.id"
        [userName]="u.name"
        [avatarUrl]="u.avatarUrl">
      </app-public-calendar>
    </main>
  </div>
  `
})
export class FeedPageComponent implements OnInit {
  private usersSvc = inject(UsersService);

  users: UserView[] = [];

  ngOnInit(): void {
    this.usersSvc.getOthers(MY_ID).subscribe({
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
