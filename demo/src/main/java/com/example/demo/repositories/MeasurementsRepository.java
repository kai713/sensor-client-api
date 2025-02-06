package com.example.demo.repositories;

import com.example.demo.entity.Measurements;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для работы с сущностью Measurements
 */
@Repository
public interface MeasurementsRepository extends JpaRepository<Measurements, Integer> {
}
