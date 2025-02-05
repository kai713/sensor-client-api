package com.example.demo.repositories;

import com.example.demo.entity.Measurements;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MeasurementsRepository extends JpaRepository<Measurements, Integer> {
    Optional<Measurements> findBySensorName(String sensorName);
}
