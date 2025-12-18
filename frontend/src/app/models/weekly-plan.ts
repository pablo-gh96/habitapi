import { Plate } from './plate';

export interface DailyPlan {
    dayOfWeek: string;
    breakfast?: Plate;
    lunch?: Plate;
    dinner?: Plate;
    snack?: Plate;

    breakfastCompleted?: boolean;
    lunchCompleted?: boolean;
    dinnerCompleted?: boolean;
    snackCompleted?: boolean;
}

export interface WeeklyPlan {
    id: number;
    startDate: string; // YYYY-MM-DD
    userId: number;
    days: { [key: string]: DailyPlan };
}

export interface WeeklyPlanRequest {
    startDate: string;
    days: {
        [key: string]: {
            breakfastPlateId?: number;
            lunchPlateId?: number;
            dinnerPlateId?: number;
            snackPlateId?: number;

            breakfastCompleted?: boolean;
            lunchCompleted?: boolean;
            dinnerCompleted?: boolean;
            snackCompleted?: boolean;
        }
    };
}
