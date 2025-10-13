package miapp.habitapi.controllers;

import miapp.habitapi.dto.CreateHabit;
import miapp.habitapi.models.Habit;
import miapp.habitapi.service.HabitService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            List<Habit> habits = habitService.getHabitsForMonth(userId, year, month);
            return ResponseEntity.ok(habits);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/habits
     * Body: CreateHabit { title, icon, date, repeat, userId }
     * Crea una o varias ocurrencias para el usuario indicado en el body.
     */
    @PostMapping
    public ResponseEntity<?> createHabits(@RequestBody CreateHabit request) {
        try {
            List<Habit> created = habitService.createHabits(request);
            return ResponseEntity.status(201).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PATCH /api/habits/{id}/status?userId=1
     * Cicla el estado del hábito asegurando pertenencia a userId.
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> toggleHabitStatus(
            @PathVariable Long id,
            @RequestParam Long userId
    ) {
        try {
            Habit updated = habitService.toggleStatus(userId, id);
            return ResponseEntity.ok(updated);
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
            @RequestParam Long userId,
            @RequestParam String title
    ) {
        try {
            long deleted = habitService.deleteByTitle(userId, title);
            return ResponseEntity.ok(Map.of("deleted", deleted));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /api/habits/{id}?userId=1
     * Borra una ocurrencia asegurando pertenencia a userId.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHabit(
            @PathVariable Long id,
            @RequestParam Long userId
    ) {
        try {
            habitService.deleteHabit(userId, id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
