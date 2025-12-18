import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { WeeklyPlanService } from '../../../../services/weekly-plan.service';
import { PlateService } from '../../../../services/plate.service';
import { Plate } from '../../../../models/plate';
import { WeeklyPlan, WeeklyPlanRequest, DailyPlan } from '../../../../models/weekly-plan';

@Component({
    selector: 'app-weekly-planner',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './weekly-planner.component.html',
    styleUrl: './weekly-planner.component.css'
})
export class WeeklyPlannerComponent implements OnInit {
    week!: WeeklyPlan;
    plates: Plate[] = [];
    currentDate: Date = new Date();
    daysOfWeek = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'];

    constructor(
        private weeklyPlanService: WeeklyPlanService,
        private plateService: PlateService
    ) { }

    ngOnInit(): void {
        this.currentDate = this.getMonday(new Date());
        this.plateService.getAllPlates().subscribe(data => this.plates = data);
        this.loadWeek();
    }

    getMonday(d: Date): Date {
        const date = new Date(d);
        const day = date.getDay();
        const diff = date.getDate() - day + (day === 0 ? -6 : 1);
        return new Date(date.setDate(diff));
    }

    loadWeek(): void {
        const mondayStr = this.currentDate.toISOString().split('T')[0];
        this.weeklyPlanService.getWeek(mondayStr).subscribe(plan => {
            this.week = plan;
            if (!this.week.days) this.week.days = {};

            // Inicialización de seguridad para que el HTML no de error de undefined
            this.daysOfWeek.forEach(day => {
                if (!this.week.days[day]) {
                    this.week.days[day] = {
                        dayOfWeek: day,
                        breakfastCompleted: false,
                        lunchCompleted: false,
                        snackCompleted: false,
                        dinnerCompleted: false
                    } as DailyPlan;
                }
            });
        });
    }

    // Métodos de ayuda para los selectores
    getPlateId(day: string, type: 'breakfast' | 'lunch' | 'snack' | 'dinner'): number | undefined {
        return this.week?.days[day]?.[type]?.id;
    }


    previousWeek() {
        this.currentDate = new Date(this.currentDate.setDate(this.currentDate.getDate() - 7));
        this.loadWeek();
    }

    nextWeek() {
        this.currentDate = new Date(this.currentDate.setDate(this.currentDate.getDate() + 7));
        this.loadWeek();
    }

    setPlateId(day: string, type: 'breakfast' | 'lunch' | 'snack' | 'dinner', plateId: any): void {
        if (!this.week.days[day]) return;

        const id = (plateId === 'undefined' || plateId === null) ? undefined : parseInt(plateId);
        const plate = this.plates.find(p => p.id === id);

        (this.week.days[day] as any)[type] = plate;

        // GUARDADO AUTOMÁTICO al cambiar el plato
        this.savePlan();
    }

    savePlan(): void {
        if (!this.week) return;

        const request: WeeklyPlanRequest = {
            startDate: this.week.startDate,
            days: {}
        };

        this.daysOfWeek.forEach(day => {
            const daily = this.week.days[day];
            if (daily) {
                request.days[day] = {
                    breakfastPlateId: daily.breakfast?.id,
                    lunchPlateId: daily.lunch?.id,
                    dinnerPlateId: daily.dinner?.id,
                    snackPlateId: daily.snack?.id,
                    breakfastCompleted: !!daily.breakfastCompleted,
                    lunchCompleted: !!daily.lunchCompleted,
                    dinnerCompleted: !!daily.dinnerCompleted,
                    snackCompleted: !!daily.snackCompleted,
                };
            }
        });


        this.weeklyPlanService.updateWeek(this.week.id, request).subscribe(updated => {
            this.week = updated;
            console.log('Plan auto-guardado');
        });
    }

}