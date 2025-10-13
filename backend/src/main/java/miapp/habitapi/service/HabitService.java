package miapp.habitapi.service;

import miapp.habitapi.dto.CreateHabit;
import miapp.habitapi.dto.RepeatType;
import miapp.habitapi.models.Habit;
import miapp.habitapi.models.Status;
import miapp.habitapi.repository.HabitRepository;
import miapp.habitapi.repository.UserRepository;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Service
public class HabitService {

    private final HabitRepository habitRepository;
    private final UserRepository userRepository;

    public HabitService(HabitRepository habitRepository, UserRepository userRepository) {
        this.habitRepository = habitRepository;
        this.userRepository = userRepository;
    }

    /**
     * Devuelve todos los hábitos del mes indicado para un userId.
     * Si no se pasa year/month, usa el mes actual.
     */
    public List<Habit> getHabitsForMonth(Long userId, Integer year, Integer month) {
        if (userId == null) throw new IllegalArgumentException("userId is required");

        YearMonth ym = (year != null && month != null) ? YearMonth.of(year, month) : YearMonth.now();
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        return habitRepository.findByUserIdAndDateBetweenOrderByTitleAsc(userId, start, end);
    }


    /**
     * Crea hábitos (una vez / diario / semanal / mensual) para el userId incluido en el DTO.
     * Expande solo dentro del AÑO de la fecha base.
     */
    @Transactional
    public List<Habit> createHabits(CreateHabit req) {
        if (req == null) throw new IllegalArgumentException("body is required");
        if (req.getUserId() == null) throw new IllegalArgumentException("userId is required");
        if (req.getDate() == null) throw new IllegalArgumentException("date is required");

        var user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id " + req.getUserId()));

        LocalDate base = req.getDate();
        List<LocalDate> dates = expandDatesForYear(base, req.getRepeat());

        List<Habit> toSave = new ArrayList<>(dates.size());
        for (LocalDate d : dates) {
            Habit h = new Habit();
            h.setTitle(req.getTitle());
            h.setIcon(req.getIcon());
            h.setStatus(Status.UNDEFINED); // estado inicial
            h.setDate(d);
            h.setUser(user);               // <- asignación de usuario
            toSave.add(h);
        }
        return habitRepository.saveAll(toSave);
    }

    /** Genera las fechas a crear en el AÑO del 'baseDate', según repeat */
    private List<LocalDate> expandDatesForYear(LocalDate baseDate, RepeatType repeat) {
        return switch (repeat) {
            case ONCE -> List.of(baseDate);

            case DAILY -> {
                Year year = Year.of(baseDate.getYear());
                LocalDate start = year.atDay(1);
                LocalDate end = year.atDay(year.length());
                List<LocalDate> days = new ArrayList<>(year.length());
                for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
                    days.add(d);
                }
                yield days;
            }

            case WEEKLY -> {
                int y = baseDate.getYear();
                DayOfWeek targetDow = baseDate.getDayOfWeek();

                LocalDate cursor = LocalDate.of(y, 1, 1);
                LocalDate first = cursor.with(TemporalAdjusters.nextOrSame(targetDow));
                LocalDate end = LocalDate.of(y, 12, 31);

                List<LocalDate> weeks = new ArrayList<>(60);
                for (LocalDate d = first; !d.isAfter(end); d = d.plusWeeks(1)) {
                    weeks.add(d);
                }
                yield weeks;
            }

            case MONTHLY -> {
                int y = baseDate.getYear();
                int dayOfMonth = baseDate.getDayOfMonth();
                List<LocalDate> months = new ArrayList<>(12);
                for (int m = 1; m <= 12; m++) {
                    YearMonth ym = YearMonth.of(y, m);
                    int dom = Math.min(dayOfMonth, ym.lengthOfMonth());
                    months.add(ym.atDay(dom));
                }
                yield months;
            }
        };
    }

    /**
     * Cicla el estado de un hábito del usuario (UNDEFINED → DONE → PARTIALLY → NOT_DONE → UNDEFINED).
     */
    @Transactional
    public Habit toggleStatus(Long userId, Long habitId) {
        if (userId == null || habitId == null) {
            throw new IllegalArgumentException("userId and habitId are required");
        }

        Habit habit = habitRepository.findByIdAndUserId(habitId, userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Habit not found with id " + habitId + " for user " + userId));

        Status current = habit.getStatus();
        Status next = switch (current) {
            case UNDEFINED -> Status.DONE;
            case DONE -> Status.PARTIALLY;
            case PARTIALLY -> Status.NOT_DONE;
            case NOT_DONE -> Status.UNDEFINED;
        };

        habit.setStatus(next);
        return habitRepository.save(habit);
    }

    /**
     * Borra un hábito por id, asegurando pertenencia a userId.
     */
    @Transactional
    public void deleteHabit(Long userId, Long habitId) {
        if (userId == null || habitId == null) {
            throw new IllegalArgumentException("userId and habitId are required");
        }
        if (!habitRepository.existsByIdAndUserId(habitId, userId)) {
            throw new IllegalArgumentException(
                    "Habit not found with id " + habitId + " for user " + userId);
        }
        habitRepository.deleteById(habitId);
    }

    /**
     * Borra todas las ocurrencias por título para un userId.
     * Devuelve el número de filas afectadas.
     */
    @Transactional
    public long deleteByTitle(Long userId, String title) {
        if (userId == null) throw new IllegalArgumentException("userId is required");
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("title is required");
        }
        String t = title.trim();

        // Opción A: obtener ids y borrar en batch (mantiene tu patrón original)
        var ids = habitRepository.findIdsByUserIdAndTitle(userId, t);
        if (ids.isEmpty()) return 0L;
        habitRepository.deleteAllByIdInBatch(ids);
        return ids.size();

        // Opción B: una sola sentencia:
        // return habitRepository.deleteByUserIdAndTitle(userId, t);
    }
}
