export class CreateHabit {
  title!: string;   
  icon!: string;   
  date!: string;  
  repeat!: "ONCE" | "DAILY" | "WEEKLY" | "MONTHLY"
  userId!: number;
}