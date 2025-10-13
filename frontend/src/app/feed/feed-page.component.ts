// src/app/feed/feed-page.component.ts
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PublicCalendarComponent } from './public-calendar.component';

type UserView = { id: number; name: string; avatarUrl: string };

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
export class FeedPageComponent {
  users: UserView[] = [
    { id: 1, name: 'Pablo',  avatarUrl: 'https://api.dicebear.com/9.x/initials/svg?seed=Pablo' },
    { id: 2, name: 'María',  avatarUrl: 'https://api.dicebear.com/9.x/initials/svg?seed=Maria' },
    { id: 3, name: 'Carlos', avatarUrl: 'https://api.dicebear.com/9.x/initials/svg?seed=Carlos' },
  ];
}
