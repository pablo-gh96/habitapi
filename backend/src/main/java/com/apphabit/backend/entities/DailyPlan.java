package com.apphabit.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "daily_plans")
public class DailyPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "weekly_plan_id")
    @JsonIgnore
    private WeeklyPlan weeklyPlan;

    @Column(nullable = false)
    private String dayOfWeek;

    @ManyToOne
    @JoinColumn(name = "breakfast_plate_id")
    private Plate breakfast;

    @ManyToOne
    @JoinColumn(name = "lunch_plate_id")
    private Plate lunch;

    @ManyToOne
    @JoinColumn(name = "dinner_plate_id")
    private Plate dinner;

    @ManyToOne
    @JoinColumn(name = "snack_plate_id")
    private Plate snack;

    private Boolean breakfastCompleted = false;
    private Boolean lunchCompleted = false;
    private Boolean dinnerCompleted = false;
    private Boolean snackCompleted = false;

    public DailyPlan() {
    }

    public DailyPlan(WeeklyPlan weeklyPlan, String dayOfWeek) {
        this.weeklyPlan = weeklyPlan;
        this.dayOfWeek = dayOfWeek;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public WeeklyPlan getWeeklyPlan() {
        return weeklyPlan;
    }

    public void setWeeklyPlan(WeeklyPlan weeklyPlan) {
        this.weeklyPlan = weeklyPlan;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Plate getBreakfast() {
        return breakfast;
    }

    public void setBreakfast(Plate breakfast) {
        this.breakfast = breakfast;
    }

    public Plate getLunch() {
        return lunch;
    }

    public void setLunch(Plate lunch) {
        this.lunch = lunch;
    }

    public Plate getDinner() {
        return dinner;
    }

    public void setDinner(Plate dinner) {
        this.dinner = dinner;
    }

    public Plate getSnack() {
        return snack;
    }

    public void setSnack(Plate snack) {
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
