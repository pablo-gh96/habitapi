package com.apphabit.backend.controllers;

import com.apphabit.backend.models.WeeklyPlanRequest;
import com.apphabit.backend.models.WeeklyPlanResponse;
import com.apphabit.backend.services.WeeklyPlanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/weeks")
@CrossOrigin(origins = "*")
public class WeeklyPlanController {

    private final WeeklyPlanService weeklyPlanService;

    public WeeklyPlanController(WeeklyPlanService weeklyPlanService) {
        this.weeklyPlanService = weeklyPlanService;
    }

    @GetMapping
    public ResponseEntity<WeeklyPlanResponse> getWeek(@RequestParam String startDate) {
        // format expected: yyyy-MM-dd
        LocalDate date = LocalDate.parse(startDate);
        return ResponseEntity.ok(weeklyPlanService.getOrCreatePlan(date));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WeeklyPlanResponse> updateWeek(@PathVariable Long id,
            @RequestBody WeeklyPlanRequest request) {
        return ResponseEntity.ok(weeklyPlanService.updatePlan(id, request));
    }
}
