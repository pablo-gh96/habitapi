import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PlateService } from '../../../../services/plate.service';
import { Plate } from '../../../../models/plate';

@Component({
    selector: 'app-plate-list',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './plate-list.component.html',
    styleUrl: './plate-list.component.css'
})
export class PlateListComponent implements OnInit {
    plates: Plate[] = [];
    newPlateName: string = '';
    newPlateType: string = '';

    constructor(private plateService: PlateService) { }

    ngOnInit(): void {
        this.loadPlates();
    }

    loadPlates(): void {
        this.plateService.getAllPlates().subscribe(data => {
            this.plates = data;
        });
    }

    addPlate(): void {
        if (!this.newPlateName.trim()) return;

        this.plateService.createPlate({ name: this.newPlateName, type: this.newPlateType })
            .subscribe(plate => {
                this.plates.push(plate);
                this.newPlateName = '';
                this.newPlateType = '';
            });
    }

    deletePlate(id: number): void {
        this.plateService.deletePlate(id).subscribe(() => {
            this.plates = this.plates.filter(p => p.id !== id);
        });
    }
}
