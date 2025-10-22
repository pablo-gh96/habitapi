import { Component, Input, OnInit, computed, effect, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, FormGroup } from '@angular/forms';
import { HabitService } from '../../services/habit.services';
import { Habit } from '../../models/habit';
import { CommentService } from '../../services/comment.service';
import { CommentResponse} from '../../dto/commentResponse';

type DayCell = { date: Date; inCurrentMonth: boolean; isToday: boolean; };

@Component({
  selector: 'app-public-calendar',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './public-calendar.component.html'
})
export class PublicCalendarComponent implements OnInit {
  @Input() userId!: number;
  @Input() userName!: string;
  @Input() avatarUrl!: string;

  // calendario
  readonly baseDate = signal(startOfMonth(new Date()));
  readonly monthLabel = computed(() =>
    this.baseDate().toLocaleDateString('es-ES', { month: 'long', year: 'numeric' })
  );
  readonly weeks = computed<DayCell[][]>(() => buildMonthMatrix(this.baseDate()));


  // hábitos (solo lectura)
  private readonly habitsByDate = signal<Map<string, Habit[]>>(new Map());
  getHabitsFor(day: Date): Habit[] {
    const key = toLocalKey(day);
    return this.habitsByDate().get(key) ?? [];
  }

  prevMonth() { this.baseDate.set(updateMonth(this.baseDate(), -1)); }
  nextMonth() { this.baseDate.set(updateMonth(this.baseDate(), +1)); }

  // comentarios del día
  readonly showCommentsModal = signal(false);
  readonly comments = signal<CommentResponse[]>([]);
  readonly commentsLoading = signal(false);
  readonly sendingComment = signal(false);
  commentForm!: FormGroup;
  readonly selectedDay = signal<Date | null>(null);

  constructor(
    private habitService: HabitService,
    private fb: FormBuilder,
    private commentService: CommentService
  ) {}

  ngOnInit(): void {
    // form para enviar comentario
    this.commentForm = this.fb.group({
      message: ['', [Validators.required, Validators.maxLength(1000)]]
    });
  }

  // cargar hábitos cuando cambian mes o userId
  private _loadEff = effect(() => {
    const current = this.baseDate();
    const uid = this.userId; // reactivo por Input en Angular 17+: si no, invoca manual en ngOnChanges
    if (!uid) return;

    const year = current.getFullYear();
    const month = current.getMonth() + 1;

    this.habitService.getHabits(uid, year, month).subscribe({
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
  });

  // abrir/cerrar modal de comentarios
  openCommentsModal(d: Date) {
    this.selectedDay.set(d);
    this.showCommentsModal.set(true);
    this.commentForm.reset({ message: '' });
    this.fetchComments(toLocalKey(d));
  }
  closeCommentsModal() {
    this.showCommentsModal.set(false);
    this.comments.set([]);
    this.commentForm.reset({ message: '' });
  }

  // pedir comentarios del día al backend
  private fetchComments(dayISO: string) {
    this.commentsLoading.set(true);
    this.commentService.getForDay(this.userId, dayISO).subscribe({
      next: (list) => this.comments.set(list), // backend devuelve ASC (antiguos primero)
      error: (err) => { console.error('❌ Error cargando comentarios:', err); this.comments.set([]); },
      complete: () => this.commentsLoading.set(false)
    });
  }

  // enviar comentario (solo añade comentarios, nada de editar/borrar)
  sendComment() {
    const msg = this.commentForm.get('message')?.value?.trim();
    const sel = this.selectedDay();
    if (!msg || !sel || this.sendingComment()) return;

    const dayISO = toLocalKey(sel);
    this.sendingComment.set(true);
    this.commentService.create({
      message: msg,
      fromUserId: this.userId,   // o el emisor real si tienes auth; aquí lo mando al propio dueño si no hay auth
      toUserId: this.userId,
      day: dayISO
    }).subscribe({
      next: () => {
        this.fetchComments(dayISO);       // recarga desde backend
        this.commentForm.reset({ message: '' });
      },
      error: (err) => {
        console.error('❌ Error creando comentario:', err);
        alert('No se pudo enviar el comentario');
      },
      complete: () => this.sendingComment.set(false)
    });
  }

  // estilos
  statusClasses(status: Habit['status']): string {
    switch (status) {
      case 'done':       return 'bg-emerald-500 ring-emerald-600 text-white';
      case 'partially':  return 'bg-amber-400 ring-amber-500 text-gray-900';
      case 'not_done':   return 'bg-rose-500 ring-rose-600 text-white';
      default:           return 'bg-gray-300 ring-gray-400 text-gray-800';
    }
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
