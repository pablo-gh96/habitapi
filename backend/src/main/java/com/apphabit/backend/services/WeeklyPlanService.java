package com.apphabit.backend.services;

import com.apphabit.backend.entities.DailyPlan;
import com.apphabit.backend.entities.Plate;
import com.apphabit.backend.entities.User;
import com.apphabit.backend.entities.WeeklyPlan;
import com.apphabit.backend.models.WeeklyPlanRequest;
import com.apphabit.backend.models.WeeklyPlanResponse;
import com.apphabit.backend.models.PlateResponse;
import com.apphabit.backend.repositories.PlateRepository;
import com.apphabit.backend.repositories.UserRepository;
import com.apphabit.backend.repositories.WeeklyPlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WeeklyPlanService {

    private final WeeklyPlanRepository weeklyPlanRepository;
    private final UserRepository userRepository;
    private final PlateRepository plateRepository;

    public WeeklyPlanService(WeeklyPlanRepository weeklyPlanRepository, UserRepository userRepository,
            PlateRepository plateRepository) {
        this.weeklyPlanRepository = weeklyPlanRepository;
        this.userRepository = userRepository;
        this.plateRepository = plateRepository;
    }

    @Transactional(readOnly = true)
    public List<WeeklyPlanResponse> getAllPlans() {
        User user = getUser();
        return weeklyPlanRepository.findByUserId(user.getId()).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public WeeklyPlanResponse getOrCreatePlan(LocalDate startDate) {
        User user = getUser();
        return weeklyPlanRepository.findByUserIdAndStartDate(user.getId(), startDate)
                .map(this::toResponse)
                .orElseGet(() -> createPlan(user, startDate));
    }

    @Transactional
    public WeeklyPlanResponse createPlan(User user, LocalDate startDate) {
        WeeklyPlan plan = new WeeklyPlan(startDate, user);
        return toResponse(weeklyPlanRepository.save(plan));
    }

    @Transactional
    public WeeklyPlanResponse updatePlan(Long planId, WeeklyPlanRequest request) {
        User user = getUser();
        WeeklyPlan plan = weeklyPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        if (!plan.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        plan.getDayPlans().clear();

        if (request.getDays() != null) {
            for (Map.Entry<String, WeeklyPlanRequest.DailyPlanDTO> entry : request.getDays().entrySet()) {
                String dayName = entry.getKey();
                WeeklyPlanRequest.DailyPlanDTO dto = entry.getValue();

                DailyPlan daily = new DailyPlan(plan, dayName);
                if (dto.getBreakfastPlateId() != null)
                    daily.setBreakfast(plateRepository.findById(dto.getBreakfastPlateId()).orElse(null));
                if (dto.getLunchPlateId() != null)
                    daily.setLunch(plateRepository.findById(dto.getLunchPlateId()).orElse(null));
                if (dto.getDinnerPlateId() != null)
                    daily.setDinner(plateRepository.findById(dto.getDinnerPlateId()).orElse(null));
                if (dto.getSnackPlateId() != null)
                    daily.setSnack(plateRepository.findById(dto.getSnackPlateId()).orElse(null));

                daily.setBreakfastCompleted(Boolean.TRUE.equals(dto.getBreakfastCompleted()));
                daily.setLunchCompleted(Boolean.TRUE.equals(dto.getLunchCompleted()));
                daily.setDinnerCompleted(Boolean.TRUE.equals(dto.getDinnerCompleted()));
                daily.setSnackCompleted(Boolean.TRUE.equals(dto.getSnackCompleted()));

                plan.getDayPlans().put(dayName, daily);
            }
        }

        return toResponse(weeklyPlanRepository.save(plan));
    }

    private WeeklyPlanResponse toResponse(WeeklyPlan plan) {
        WeeklyPlanResponse res = new WeeklyPlanResponse();
        res.setId(plan.getId());
        res.setUserId(plan.getUser().getId());
        res.setStartDate(plan.getStartDate());

        Map<String, WeeklyPlanResponse.DailyPlanResponse> days = new HashMap<>();
        if (plan.getDayPlans() != null) {
            for (Map.Entry<String, DailyPlan> entry : plan.getDayPlans().entrySet()) {
                DailyPlan dp = entry.getValue();
                PlateResponse breakfast = dp.getBreakfast() != null
                        ? new PlateResponse(dp.getBreakfast().getId(), dp.getBreakfast().getName(),
                                dp.getBreakfast().getType())
                        : null;
                PlateResponse lunch = dp.getLunch() != null
                        ? new PlateResponse(dp.getLunch().getId(), dp.getLunch().getName(), dp.getLunch().getType())
                        : null;
                PlateResponse dinner = dp.getDinner() != null
                        ? new PlateResponse(dp.getDinner().getId(), dp.getDinner().getName(), dp.getDinner().getType())
                        : null;
                PlateResponse snack = dp.getSnack() != null
                        ? new PlateResponse(dp.getSnack().getId(), dp.getSnack().getName(), dp.getSnack().getType())
                        : null;

                days.put(entry.getKey(),
                        new WeeklyPlanResponse.DailyPlanResponse(dp.getDayOfWeek(), breakfast, lunch, dinner, snack,
                                dp.getBreakfastCompleted(), dp.getLunchCompleted(), dp.getDinnerCompleted(),
                                dp.getSnackCompleted()));
            }
        }
        res.setDays(days);
        return res;
    }

    private User getUser() {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();
        String username = (String) auth.getPrincipal();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }
}
