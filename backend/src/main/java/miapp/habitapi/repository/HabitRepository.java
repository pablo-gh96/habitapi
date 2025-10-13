package miapp.habitapi.repository;


import miapp.habitapi.models.Habit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HabitRepository extends JpaRepository<Habit, Long> {

    // Buscar todos los hábitos de un día concreto
    List<Habit> findByDate(LocalDate date);

    // Buscar por nombre (útil si un hábito se repite)
    List<Habit> findByTitle(String title);

    // Buscar por rango de fechas (por ejemplo el mes actual)
    List<Habit> findByDateBetween(LocalDate start, LocalDate end);
    
    long countByTitle(String title);

    @Query("select h.id from Habit h where h.title = :title")
    List<Long> findIdsByTitle(@Param("title") String title);
}

