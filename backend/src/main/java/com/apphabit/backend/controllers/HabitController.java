package com.apphabit.backend.controllers;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.apphabit.backend.entities.Habit;
import com.apphabit.backend.models.CreateHabitRequest;
import com.apphabit.backend.services.HabitService;
import com.apphabit.backend.services.JpaUserDetailsService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/habits")
@CrossOrigin(origins = "*")
public class HabitController {

    private final HabitService habitService;

    public HabitController(HabitService habitService) {
        this.habitService = habitService;
    }


    @PostMapping
    public ResponseEntity<?> create(@Validated @RequestBody CreateHabitRequest req) {
    	List<Habit> saved = habitService.createHabits(req); // overload por username
    	return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }


    /**
     * DELETE /api/habits/{id}?userId=1
     * Borra una ocurrencia asegurando pertenencia a userId.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHabit(
            @PathVariable Long id
    ) {
        try {
            habitService.deleteHabit(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * DELETE /api/habits/by-title?userId=1&title=Gym
     * Borra todas las ocurrencias de ese título para el usuario.
     */
    @DeleteMapping("/by-title")
    public ResponseEntity<?> deleteByTitle(
            @RequestParam String title
    ) {
        try {
            long deleted = habitService.deleteByTitle(title);
            return ResponseEntity.ok(Map.of("deleted", deleted));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * PATCH /api/habits/{id}/status?userId=1
     * Cicla el estado del hábito asegurando pertenencia a userId.
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> toggleHabitStatus(
            @PathVariable Long id
    ) {
        try {
            Habit updated = habitService.toggleStatus(id);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * GET /api/habits?userId=1&year=2025&month=10
     * Devuelve todos los hábitos del mes indicado para ese usuario.
     * Si no se pasan year/month, usa el mes actual.
     */
    @GetMapping
    public ResponseEntity<?> getHabitsForMonth(
    		@RequestParam Long userId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
    ) {
        try {
            List<Habit> habits = habitService.getHabitsForMonth(userId,year, month);
            return ResponseEntity.ok(habits);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
