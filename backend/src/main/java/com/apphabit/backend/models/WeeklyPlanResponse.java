package com.apphabit.backend.models;

import java.time.LocalDate;
import java.util.Map;

public class WeeklyPlanResponse {
    private Long id;
    private LocalDate startDate;
    private Long userId;
    private Map<String, DailyPlanResponse> days;

    public WeeklyPlanResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Map<String, DailyPlanResponse> getDays() {
        return days;
    }

    public void setDays(Map<String, DailyPlanResponse> days) {
        this.days = days;
    }

    public static class DailyPlanResponse {
        private String dayOfWeek;
        private PlateResponse breakfast;
        private PlateResponse lunch;
        private PlateResponse dinner;
        private PlateResponse snack;

        private Boolean breakfastCompleted;
        private Boolean lunchCompleted;
        private Boolean dinnerCompleted;
        private Boolean snackCompleted;

        public DailyPlanResponse(String dayOfWeek, PlateResponse breakfast, PlateResponse lunch, PlateResponse dinner,
                PlateResponse snack,
                Boolean breakfastCompleted, Boolean lunchCompleted, Boolean dinnerCompleted, Boolean snackCompleted) {
            this.dayOfWeek = dayOfWeek;
            this.breakfast = breakfast;
            this.lunch = lunch;
            this.dinner = dinner;
            this.snack = snack;
            this.breakfastCompleted = breakfastCompleted;
            this.lunchCompleted = lunchCompleted;
            this.dinnerCompleted = dinnerCompleted;
            this.snackCompleted = snackCompleted;
        }

        public String getDayOfWeek() {
            return dayOfWeek;
        }

        public void setDayOfWeek(String dayOfWeek) {
            this.dayOfWeek = dayOfWeek;
        }

        public PlateResponse getBreakfast() {
            return breakfast;
        }

        public void setBreakfast(PlateResponse breakfast) {
            this.breakfast = breakfast;
        }

        public PlateResponse getLunch() {
            return lunch;
        }

        public void setLunch(PlateResponse lunch) {
            this.lunch = lunch;
        }

        public PlateResponse getDinner() {
            return dinner;
        }

        public void setDinner(PlateResponse dinner) {
            this.dinner = dinner;
        }

        public PlateResponse getSnack() {
            return snack;
        }

        public void setSnack(PlateResponse snack) {
            this.snack = snack;
        }

        public Boolean getBreakfastCompleted() {
            return breakfastCompleted;
        }

        public void setBreakfastCompleted(Boolean breakfastCompleted) {
            this.breakfastCompleted = breakfastCompleted;
        }

        public Boolean getLunchCompleted() {
            return lunchCompleted;
        }

        public void setLunchCompleted(Boolean lunchCompleted) {
            this.lunchCompleted = lunchCompleted;
        }

        public Boolean getDinnerCompleted() {
            return dinnerCompleted;
        }

        public void setDinnerCompleted(Boolean dinnerCompleted) {
            this.dinnerCompleted = dinnerCompleted;
        }

        public Boolean getSnackCompleted() {
            return snackCompleted;
        }

        public void setSnackCompleted(Boolean snackCompleted) {
            this.snackCompleted = snackCompleted;
        }
    }
}
