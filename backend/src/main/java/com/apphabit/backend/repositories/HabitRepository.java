package com.apphabit.backend.repositories;


import com.apphabit.backend.entities.Habit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HabitRepository extends JpaRepository<Habit, Long> {

    // Listado por usuario y rango de fechas (mes, semana, etc.)
    List<Habit> findByUserIdAndDateBetween(Long userId, LocalDate start, LocalDate end);

    // Un hábito por id asegurando pertenencia
    Optional<Habit> findByIdAndUserId(Long id, Long userId);

    // Existencia asegurando pertenencia
    boolean existsByIdAndUserId(Long id, Long userId);

    // IDs por título y usuario (para borrado masivo)
    @Query("select h.id from Habit h where h.user.id = :userId and h.title = :title")
    List<Long> findIdsByUserIdAndTitle(Long userId, String title);

    // (Alternativa directa si prefieres no hacer findIds + batch)
    long deleteByUserIdAndTitle(Long userId, String title);
    
    List<Habit> findByUserIdAndDateBetweenOrderByTitleAsc(
    	    Long userId, LocalDate start, LocalDate end
    	);

}

