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
@CrossOrigin(origins = "*") // opcional, si tu front está separado (Angular)
public class HabitController {

    private final HabitService habitService;

    public HabitController(HabitService habitService) {
        this.habitService = habitService;
    }

    /**
     * GET /api/habits?year=2025&month=10
     * Devuelve todos los hábitos del mes indicado.
     * Si no se pasan parámetros, usa el mes actual.
     */
    @GetMapping
    public ResponseEntity<List<Habit>> getHabitsForMonth(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
    ) {
        List<Habit> habits = habitService.getHabitsForMonth(year, month);
        return ResponseEntity.ok(habits);
    }
    
    @PostMapping
    public ResponseEntity<List<Habit>> createHabits(@RequestBody CreateHabit request) {
        List<Habit> created = habitService.createHabits(request);
        return ResponseEntity.status(201).body(created);
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<Habit> toggleHabitStatus(@PathVariable Long id) {
        try {
            Habit updated = habitService.toggleStatus(id);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/by-title")
    public ResponseEntity<Map<String, Object>> deleteByTitle(@RequestParam String title) {
        long deleted = habitService.deleteByTitle(title);
        return ResponseEntity.ok(Map.of("deleted", deleted));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHabit(@PathVariable Long id) {
        habitService.deleteHabit(id);
        return ResponseEntity.noContent().build(); // 204
    }
    
}

