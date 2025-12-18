import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PlateListComponent } from './components/plate-list/plate-list.component';
import { WeeklyPlannerComponent } from './components/weekly-planner/weekly-planner.component';

@Component({
    selector: 'app-comidas',
    standalone: true,
    imports: [CommonModule, PlateListComponent, WeeklyPlannerComponent],
    templateUrl: './comidas.component.html',
    styleUrl: './comidas.component.css'
})
export class ComidasComponent { }
