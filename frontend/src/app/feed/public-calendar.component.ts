import { Component, computed, effect, signal, OnInit, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { Habit } from '../models/habit';
import { HabitService } from '../services/habit.services';

type DayCell = { date: Date; inCurrentMonth: boolean; isToday: boolean; };

@Component({
  selector: 'app-public-calendar',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './public-calendar.component.html'
})
export class PublicCalendarComponent implements OnInit {

  /* 👇 estos vienen de fuera (feed) */
  @Input() userId!: number;
  @Input() userName: string = 'Usuario';
  @Input() avatarUrl: string = 'https://api.dicebear.com/9.x/initials/svg?seed=User';

  readonly baseDate = signal(startOfMonth(new Date()));
  readonly monthLabel = computed(() =>
    this.baseDate().toLocaleDateString('es-ES', { month: 'long', year: 'numeric' })
  );
  readonly weeks = computed<DayCell[][]>(() => buildMonthMatrix(this.baseDate()));

  private readonly habitsByDate = signal<Map<string, Habit[]>>(new Map());
  getHabitsFor(day: Date): Habit[] {
    const key = toLocalKey(day);
    return this.habitsByDate().get(key) ?? [];
  }

  prevMonth() { this.baseDate.set(updateMonth(this.baseDate(), -1)); }
  nextMonth() { this.baseDate.set(updateMonth(this.baseDate(), +1)); }

  constructor(private habitService: HabitService) {}

  ngOnInit(): void {
    // no hay formulario ni creación aquí
  }

  private _loadEff = effect(() => {
    const current = this.baseDate();
    if (!this.userId) return;
    const year = current.getFullYear();
    const month = current.getMonth() + 1;
    this.loadMonth(year, month);
  });

  private loadMonth(year: number, month: number): void {
    this.habitService.getHabits(this.userId, year, month).subscribe({
      next: (items) => {
        const map = new Map<string, Habit[]>();
        for (const raw of items) {
          const h: Habit = {
            ...raw,
            status: (raw.status as any)?.toString().toLowerCase()
          };
          const key = typeof h.date === 'string'
            ? (h.date as string).slice(0, 10)
            : toLocalKey(new Date(h.date));
          const arr = map.get(key) ?? [];
          arr.push(h);
          map.set(key, arr);
        }
        this.habitsByDate.set(map);
      },
      error: (err) => {
        console.error('❌ Error cargando hábitos:', err);
        this.habitsByDate.set(new Map());
      }
    });
  }

  // click en icono: aquí no hacemos toggle (solo lectura). Si quisieras,
  // podrías emitir un evento al padre en lugar de mutar.
  onHabitClick(_h: Habit) { /* no-op en público */ }

  statusClasses(status: Habit['status']): string {
    switch (status) {
      case 'done':       return 'bg-emerald-500 ring-emerald-600 text-white';
      case 'partially':  return 'bg-amber-400 ring-amber-500 text-gray-900';
      case 'not_done':   return 'bg-rose-500 ring-rose-600 text-white';
      default:           return 'bg-gray-300 ring-gray-400 text-gray-800';
    }
  }

  // modal de día (solo visualización)
  readonly showDayModal = signal(false);
  readonly selectedDay = signal<Date | null>(null);
  readonly selectedDayHabits = computed(() => {
    const d = this.selectedDay();
    return d ? this.getHabitsFor(d) : [];
  });
  openDayModal(d: Date) { this.selectedDay.set(d); this.showDayModal.set(true); }
  closeDayModal() { this.showDayModal.set(false); }
}

/* ===== helpers (idénticos) ===== */
function startOfMonth(d: Date): Date { return new Date(d.getFullYear(), d.getMonth(), 1); }
function updateMonth(d: Date, delta: number): Date { const nd = new Date(d); nd.setMonth(nd.getMonth() + delta); return startOfMonth(nd); }
function startOfWeekMonday(d: Date): Date { const day = (d.getDay() + 6) % 7; const sd = new Date(d); sd.setDate(d.getDate() - day); sd.setHours(0,0,0,0); return sd; }
function isSameDate(a: Date, b: Date): boolean { return a.getFullYear()===b.getFullYear() && a.getMonth()===b.getMonth() && a.getDate()===b.getDate(); }
function buildMonthMatrix(base: Date): DayCell[][] {
  const first = startOfMonth(base);
  const start = startOfWeekMonday(first);
  const today = new Date();
  const matrix: DayCell[][] = [];
  for (let i = 0; i < 42; i++) {
    const cur = new Date(start); cur.setDate(start.getDate() + i);
    const cell: DayCell = { date: cur, inCurrentMonth: cur.getMonth() === base.getMonth(), isToday: isSameDate(cur, today) };
    const w = Math.floor(i / 7); (matrix[w] ??= []).push(cell);
  }
  return matrix;
}
function toLocalKey(d: Date): string {
  const y = d.getFullYear();
  const m = (d.getMonth() + 1).toString().padStart(2, '0');
  const day = d.getDate().toString().padStart(2, '0');
  return `${y}-${m}-${day}`;
}
