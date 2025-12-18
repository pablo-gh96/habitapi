package com.apphabit.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "weekly_plans")
public class WeeklyPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate startDate; // Monday of the week

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    // Storing the plan as a simple JSON-like structure or localized fields might be
    // complex.
    // For simplicity in SQL, we can use ElementCollection or OneToMany.
    // Let's use ElementCollection for a Map<DayOfWeek, DailyMeals> if possible, or
    // flat fields.
    // Given the complexity of "lunch/dinner" and "days", let's make it a bit more
    // relational but simple access.

    // Actually, to keep it simple as requested ("entidad semana a la que se podrán
    // añadir los platos"):
    // Let's model the "slots" clearly.

    // Simplification: 7 days * 2 meals (Lunch, Dinner) = 14 slots.
    // We can map these using a separate entity or just simple Map with
    // @ElementCollection if supported,
    // but JPA ElementCollection with Entity references is tricky.

    // Alternative: A separate Entity `DailyPlan` linked to `WeeklyPlan`.

    @OneToMany(mappedBy = "weeklyPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private Map<String, DailyPlan> dayPlans = new HashMap<>();
    // Key could be "MONDAY", "TUESDAY" etc.
    // But Map<String, Entity> in OneToMany requires @MapKey

    public WeeklyPlan() {
    }

    public WeeklyPlan(LocalDate startDate, User user) {
        this.startDate = startDate;
        this.user = user;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Map<String, DailyPlan> getDayPlans() {
        return dayPlans;
    }

    public void setDayPlans(Map<String, DailyPlan> dayPlans) {
        this.dayPlans = dayPlans;
    }

    @Transient
    @JsonProperty("userId")
    public Long getUserIdForJson() {
        return (user != null) ? user.getId() : null;
    }
}
