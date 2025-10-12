import { Component, computed, effect, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, FormGroup } from '@angular/forms';
import { Habit } from '../models/habit';
import { HabitService } from '../services/habit.services';

type DayCell = { date: Date; inCurrentMonth: boolean; isToday: boolean; };

@Component({
  selector: 'app-profile-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './profile-page.component.html'
})
export class ProfilePageComponent implements OnInit {

  readonly userName = 'Pablo';
  readonly avatarUrl = 'https://api.dicebear.com/9.x/initials/svg?seed=Pablo';

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

  readonly showCreateModal = signal(false);
  createForm!: FormGroup;

  constructor(private habitService: HabitService, private fb: FormBuilder) {}

  ngOnInit(): void {
    // inicializamos el formulario dentro del ciclo de vida, ya con fb disponible
    const today = toLocalKey(new Date());
    this.createForm = this.fb.group({
      name: ['', [Validators.required, Validators.maxLength(80)]],
      icon: ['', [Validators.required, Validators.maxLength(8)]],
      date: [today, [Validators.required]],
      repeat: ['once', Validators.required]
    });
  }

  private _loadEff = effect(() => {
    const current = this.baseDate();
    const year = current.getFullYear();
    const month = current.getMonth() + 1;
    this.loadMonth(year, month);
  });

  private loadMonth(year: number, month: number): void {
    this.habitService.getHabits(year, month).subscribe({
      next: (items) => {
        const map = new Map<string, Habit[]>();
        for (const h of items) {
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

  openCreateModal() {
    const today = toLocalKey(new Date());
    this.createForm.reset({ name: '', icon: '', date: today, repeat: 'once' });
    this.showCreateModal.set(true);
  }

  closeCreateModal() {
    this.showCreateModal.set(false);
  }

  submitCreate() {
  if (this.createForm.invalid) return;

  const value = this.createForm.getRawValue();

  this.habitService.create(value).subscribe({
    next: () => {
      this.reloadCurrentMonth()
      this.closeCreateModal();
    },
    error: (err) => {
      console.error('❌ Error al crear hábito:', err);
      alert('Error al crear el hábito');
    }
  });
  
}
    onHabitClick(h: Habit) {
    this.habitService.updateStatus(h.id).subscribe({
        next: () => this.reloadCurrentMonth(),
        error: (err) => {
        console.error('❌ Error al cambiar estado:', err);
        // opcional: mostrar toast/alert
        }
    });
}

  statusClasses(status: Habit['status']): string {
    switch (status) {
      case 'done':       return 'bg-emerald-500 ring-emerald-600 text-white';
      case 'partially':  return 'bg-amber-400 ring-amber-500 text-gray-900';
      case 'not_done':   return 'bg-rose-500 ring-rose-600 text-white';
      default:           return 'bg-gray-300 ring-gray-400 text-gray-800';
    }
  }

    // --- Modal "tareas del día" ---
    readonly showDayModal = signal(false);
    readonly selectedDay = signal<Date | null>(null);

    // Habitos del día seleccionado
    readonly selectedDayHabits = computed(() => {
    const d = this.selectedDay();
    return d ? this.getHabitsFor(d) : [];
    });

    openDayModal(d: Date) {
    this.selectedDay.set(d);
    this.showDayModal.set(true);
    }
    closeDayModal() {
    this.showDayModal.set(false);
    }
  
    onDeleteHabit(h: Habit) {
    this.habitService.delete(h.id).subscribe({
        next: () => this.reloadCurrentMonth(),
        error: (err) => console.error('❌ Error al borrar hábito:', err)
    });
    }

    // Si no lo tienes aún:
    private reloadCurrentMonth() {
    const d = this.baseDate();
    this.loadMonth(d.getFullYear(), d.getMonth() + 1);
    }

    // --- Confirmación de borrado ---
    readonly showConfirmModal = signal(false);
    private habitToDelete: Habit | null = null;

    openConfirmDelete(h: Habit) {
    this.habitToDelete = h;
    this.showConfirmModal.set(true);
    }

    closeConfirmDelete() {
    this.showConfirmModal.set(false);
    this.habitToDelete = null;
    }

    // Borrar SOLO esta ocurrencia (por id)
    confirmDeleteSingle() {
    if (!this.habitToDelete) return;
    this.habitService.delete(this.habitToDelete.id).subscribe({
        next: () => {
        this.closeConfirmDelete();
        this.reloadCurrentMonth();
        },
        error: (err) => console.error('❌ Error al borrar por id:', err)
    });
    }

    // Borrar TODAS las ocurrencias con el mismo título
    confirmDeleteAll() {
    if (!this.habitToDelete) return;
    const title = this.habitToDelete.title;
    this.habitService.deleteByTitle(title).subscribe({
        next: () => {
        this.closeConfirmDelete();
        this.reloadCurrentMonth();
        },
        error: (err) => console.error('❌ Error al borrar por título:', err)
    });
    }


}

/* ===== Helpers ===== */
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

function nextStatus(s: Habit['status']): Habit['status'] {
  switch (s) {
    case 'undefined': return 'done';
    case 'done':      return 'partially';
    case 'partially': return 'not_done';
    case 'not_done':  return 'undefined';
    default:          return 'undefined';
  }
}