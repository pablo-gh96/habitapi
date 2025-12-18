package com.apphabit.backend.repositories;

import com.apphabit.backend.entities.WeeklyPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

@Repository
public interface WeeklyPlanRepository extends JpaRepository<WeeklyPlan, Long> {
    List<WeeklyPlan> findByUserId(Long userId);

    Optional<WeeklyPlan> findByUserIdAndStartDate(Long userId, LocalDate startDate);
}
