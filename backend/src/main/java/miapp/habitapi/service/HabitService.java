package miapp.habitapi.service;


import miapp.habitapi.dto.CreateHabit;
import miapp.habitapi.dto.RepeatType;
import miapp.habitapi.models.Habit;
import miapp.habitapi.repository.HabitRepository;
import miapp.habitapi.models.Status;
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

    public HabitService(HabitRepository habitRepository) {
        this.habitRepository = habitRepository;
    }

    /**
     * Devuelve todos los hábitos del mes indicado (por ejemplo octubre 2025).
     * Si no se pasa ningún parámetro, usa el mes actual.
     */
    public List<Habit> getHabitsForMonth(Integer year, Integer month) {
        // Si no se indica, usar el año/mes actual
        YearMonth ym = (year != null && month != null)
                ? YearMonth.of(year, month)
                : YearMonth.now();

        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        return habitRepository.findByDateBetween(start, end);
    }
    
    @Transactional
    public List<Habit> createHabits(CreateHabit req) {
        LocalDate base = req.getDate();
        if (base == null) throw new IllegalArgumentException("date is required");

        List<LocalDate> dates = expandDatesForYear(base, req.getRepeat());
        List<Habit> toSave = new ArrayList<>(dates.size());

        for (LocalDate d : dates) {
            Habit h = new Habit();
            h.setTitle(req.getTitle());
            h.setIcon(req.getIcon());
            h.setStatus(Status.UNDEFINED);      // estado inicial
            h.setDate(d);
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
                // primera ocurrencia del mismo día de la semana en el año (>= 1 de enero)
                LocalDate first = cursor.with(TemporalAdjusters.nextOrSame(targetDow));
                LocalDate end = LocalDate.of(y, 12, 31);

                List<LocalDate> weeks = new ArrayList<>(60); // máx ~53
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
                    int dom = Math.min(dayOfMonth, ym.lengthOfMonth()); // 31→30/28/29 si toca
                    months.add(ym.atDay(dom));
                }
                yield months;
            }
        };
    }
    
    @Transactional
    public Habit toggleStatus(Long id) {
        Habit habit = habitRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Habit not found with id " + id));

        Status current = habit.getStatus();
        Status next;

        switch (current) {
            case UNDEFINED -> next = Status.DONE;
            case DONE -> next = Status.PARTIALLY;
            case PARTIALLY -> next = Status.NOT_DONE;
            case NOT_DONE -> next = Status.UNDEFINED;
            default -> next = Status.UNDEFINED;
        }

        habit.setStatus(next);
        return habitRepository.save(habit);
    }
    
    @Transactional
    public void deleteHabit(Long id) {
        if (!habitRepository.existsById(id)) {
            throw new IllegalArgumentException("Habit not found with id " + id);
        }
        habitRepository.deleteById(id);
    }


    @Transactional
    public long deleteByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("title is required");
        }
        String t = title.trim();

        // 1) obtener ids a borrar
        var ids = habitRepository.findIdsByTitle(t);
        if (ids.isEmpty()) return 0L;

        // 2) borrar en batch
        habitRepository.deleteAllByIdInBatch(ids);

        // 3) devolver cuántos eran
        return ids.size();
    }
}

