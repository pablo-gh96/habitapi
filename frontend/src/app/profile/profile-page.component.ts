// src/app/profile-page/profile-page.component.ts
import { Component, computed, effect, signal, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, FormGroup } from '@angular/forms';
import { Habit } from '../models/habit';
import { HabitService } from '../services/habit.services';
import { CommentService} from '../services/comment.service';
import { CreateComment } from '../dto/CreateComment';
import { CommentResponse } from '../dto/commentResponse';
import { ActivatedRoute } from '@angular/router';
import { UsersService } from '../services/user.services';
import { Router } from '@angular/router';

type DayCell = { date: Date; inCurrentMonth: boolean; isToday: boolean; };




@Component({
  selector: 'app-profile-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './profile-page.component.html'
})
export class ProfilePageComponent implements OnInit {

  private USER_ID = 0;
  
  private readonly route = inject(ActivatedRoute);
  username = this.route.snapshot.paramMap.get('username') || '';
  private router = inject(Router);
  readonly avatarUrl = 'https://api.dicebear.com/9.x/initials/svg?seed=' + this.username;

  // --- Calendario base ---
  readonly baseDate = signal(startOfMonth(new Date()));
  readonly monthLabel = computed(() =>
    this.baseDate().toLocaleDateString('es-ES', { month: 'long', year: 'numeric' })
  );
  readonly weeks = computed<DayCell[][]>(() => buildMonthMatrix(this.baseDate()));

  // --- Hábitos agrupados por fecha (yyyy-MM-dd) ---
  private readonly habitsByDate = signal<Map<string, Habit[]>>(new Map());
  getHabitsFor(day: Date): Habit[] {
    const key = toLocalKey(day);
    return this.habitsByDate().get(key) ?? [];
  }

  prevMonth() { this.baseDate.set(updateMonth(this.baseDate(), -1)); }
  nextMonth() { this.baseDate.set(updateMonth(this.baseDate(), +1)); }

  // --- Modal crear hábito ---
  readonly showCreateModal = signal(false);
  createForm!: FormGroup;

  // --- Modal tareas del día ---
  readonly showDayModal = signal(false);
  readonly selectedDay = signal<Date | null>(null);
  readonly selectedDayHabits = computed(() => {
    const d = this.selectedDay();
    return d ? this.getHabitsFor(d) : [];
  });

  // --- Modales de borrado ---
  readonly showConfirmModal = signal(false);
  private habitToDelete: Habit | null = null;

  // --- Comentarios del día ---
  readonly showCommentsModal = signal(false);
  readonly comments = signal<CommentResponse[]>([]);
  readonly commentsLoading = signal(false);
  readonly sendingComment = signal(false);
  commentForm!: FormGroup;

  constructor(
    private habitService: HabitService,
    private fb: FormBuilder,
    private commentService: CommentService,
    private userService: UsersService
  ) {}

  ngOnInit(): void {
    const today = toLocalKey(new Date());
    
    this.userService.getIdByUsername(this.username).subscribe({
      next: (res) => {
        this.USER_ID = res.id;
      },
        error: (err) => {
        console.error('Usuario no encontrado:', err);
        this.router.navigate(['/not-found']); // redirige a página 404
        }
    });
    // Form crear hábito
    this.createForm = this.fb.group({
      title: ['', [Validators.required, Validators.maxLength(80)]],
      icon: ['', [Validators.required, Validators.maxLength(8)]],
      date: [today, [Validators.required]],
      repeat: ['once', Validators.required]
    });

    // Form nuevo comentario (reactivo)
    this.commentForm = this.fb.group({
      message: ['', [Validators.required, Validators.maxLength(1000)]]
    });
  }

  // Efecto: cuando cambie el mes base, recarga
  private _loadEff = effect(() => {
    const current = this.baseDate();
    const year = current.getFullYear();
    const month = current.getMonth() + 1;
    this.loadMonth(year, month);
  });

  private loadMonth(year: number, month: number): void {
    this.habitService.getHabits(this.USER_ID, year, month).subscribe({
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

  // --- Crear hábito ---
  openCreateModal() {
    const today = toLocalKey(new Date());
    this.createForm.reset({ title: '', icon: '', date: today, repeat: 'once' });
    this.showCreateModal.set(true);
  }
  closeCreateModal() { this.showCreateModal.set(false); }

  submitCreate() {
    if (this.createForm.invalid) return;

    const v = this.createForm.getRawValue();
    const payload = {
      title: v.title,
      icon: v.icon,
      date: v.date, // 'YYYY-MM-DD'
      repeat: (v.repeat as string).toUpperCase() as any, // ONCE|DAILY|WEEKLY|MONTHLY
    };

    this.habitService.create(payload as any).subscribe({
      next: () => { this.reloadCurrentMonth(); this.closeCreateModal(); },
      error: (err) => { console.error('❌ Error al crear hábito:', err); alert('Error al crear el hábito'); }
    });
  }

  // --- Interacción con hábitos ---
  onHabitClick(h: Habit) {
    this.habitService.updateStatus(this.USER_ID, h.id).subscribe({
      next: () => this.reloadCurrentMonth(),
      error: (err) => console.error('❌ Error al cambiar estado:', err)
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

  // --- Modal tareas del día ---
  openDayModal(d: Date) { this.selectedDay.set(d); this.showDayModal.set(true); }
  closeDayModal() { this.showDayModal.set(false); }

  // --- Borrado de hábitos ---
  onDeleteHabit(h: Habit) {
    this.habitService.delete(this.USER_ID, h.id).subscribe({
      next: () => this.reloadCurrentMonth(),
      error: (err) => console.error('❌ Error al borrar hábito:', err)
    });
  }

  openConfirmDelete(h: Habit) { this.habitToDelete = h; this.showConfirmModal.set(true); }
  closeConfirmDelete() { this.showConfirmModal.set(false); this.habitToDelete = null; }

  confirmDeleteSingle() {
    if (!this.habitToDelete) return;
    this.habitService.delete(this.USER_ID, this.habitToDelete.id).subscribe({
      next: () => { this.closeConfirmDelete(); this.reloadCurrentMonth(); },
      error: (err) => console.error('❌ Error al borrar por id:', err)
    });
  }
  confirmDeleteAll() {
    if (!this.habitToDelete) return;
    const title = this.habitToDelete.title;
    this.habitService.deleteByTitle(this.USER_ID, title).subscribe({
      next: () => { this.closeConfirmDelete(); this.reloadCurrentMonth(); },
      error: (err) => console.error('❌ Error al borrar por título:', err)
    });
  }

  private reloadCurrentMonth() {
    const d = this.baseDate();
    this.loadMonth(d.getFullYear(), d.getMonth() + 1);
  }

  // --- Comentarios del día ---
  // abrir modal comentarios
  openCommentsModal(d: Date) {
    this.selectedDay.set(d);
    this.showCommentsModal.set(true);

    const dayISO = toLocalKey(d);
    this.commentsLoading.set(true);
    this.commentService.getForDay(this.USER_ID, dayISO).subscribe({
      next: (list) => {
        // backend ya devuelve ASC por createdAt; no hace falta ordenar aquí
        this.comments.set(list);
      },
      error: (err) => { console.error('❌ Error cargando comentarios:', err); this.comments.set([]); },
      complete: () => this.commentsLoading.set(false)
    });

    this.commentForm.reset({ message: '' });
  }

  closeCommentsModal() {
    this.showCommentsModal.set(false);
    this.comments.set([]);
    this.commentForm.reset({ message: '' });
  }

  sendComment() {
  const msg = this.commentForm.get('message')?.value?.trim();
  const sel = this.selectedDay();
  if (!msg || !sel || this.sendingComment()) return;

  const body: CreateComment = {
    message: msg,
    fromUserId: sessionStorage.getItem('userId') ? Number(sessionStorage.getItem('userId')) : 0,
    toUserId: this.USER_ID,         // si luego eliges destinatario, cámbialo aquí
    day: toLocalKey(sel)       // 'YYYY-MM-DD'
  };

  this.sendingComment.set(true);
  this.commentService.create(body).subscribe({
    next: () => {
      const dayISO = body.day ?? toLocalKey(this.selectedDay()!);
      this.commentsLoading.set(true);
      this.commentService.getForDay(this.USER_ID, dayISO).subscribe({
        next: (list) => this.comments.set(list),   // ⬅️ lista fresca del backend
        error: (err) => { console.error('❌ Error recargando comentarios:', err); },
        complete: () => this.commentsLoading.set(false)
      });
      this.commentForm.reset({ message: '' });
    },
    error: (err) => {
      console.error('❌ Error creando comentario:', err);
      alert('No se pudo enviar el comentario');
    },
    complete: () => this.sendingComment.set(false)
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
