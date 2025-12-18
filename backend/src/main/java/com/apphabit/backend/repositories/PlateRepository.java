package com.apphabit.backend.repositories;

import com.apphabit.backend.entities.Plate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlateRepository extends JpaRepository<Plate, Long> {
    List<Plate> findByUserId(Long userId);
}
