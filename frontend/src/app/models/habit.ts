export class Habit {
    id!: number;
    title!: string;
    icon!: string;
    status!: 'done' | 'partially' | 'not_done' | 'undefined';
    date!: Date;
}