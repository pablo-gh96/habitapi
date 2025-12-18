package com.apphabit.backend.models;

import java.time.LocalDate;
import java.util.Map;

public class WeeklyPlanRequest {
    private LocalDate startDate;
    // Map of day (MONDAY) -> DailyPlanRequest
    private Map<String, DailyPlanDTO> days;

    public WeeklyPlanRequest() {
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public Map<String, DailyPlanDTO> getDays() {
        return days;
    }

    public void setDays(Map<String, DailyPlanDTO> days) {
        this.days = days;
    }

    public static class DailyPlanDTO {
        private Long breakfastPlateId;
        private Long lunchPlateId;
        private Long dinnerPlateId;
        private Long snackPlateId;

        private Boolean breakfastCompleted;
        private Boolean lunchCompleted;
        private Boolean dinnerCompleted;
        private Boolean snackCompleted;

        public Long getBreakfastPlateId() {
            return breakfastPlateId;
        }

        public void setBreakfastPlateId(Long breakfastPlateId) {
            this.breakfastPlateId = breakfastPlateId;
        }

        public Long getLunchPlateId() {
            return lunchPlateId;
        }

        public void setLunchPlateId(Long lunchPlateId) {
            this.lunchPlateId = lunchPlateId;
        }

        public Long getDinnerPlateId() {
            return dinnerPlateId;
        }

        public void setDinnerPlateId(Long dinnerPlateId) {
            this.dinnerPlateId = dinnerPlateId;
        }

        public Long getSnackPlateId() {
            return snackPlateId;
        }

        public void setSnackPlateId(Long snackPlateId) {
            this.snackPlateId = snackPlateId;
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
