package com.example.demo.repositories;

import com.example.demo.entity.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий для работы с сущностью Sensor
 */
@Repository
public interface SensorRepository extends JpaRepository<Sensor, Integer> {

    /**
     * Метод для поиска сенсора по его имени.
     *
     * @param name имя сенсора
     * @return объект Optional с найденным сенсором, если он существует, или пустой объект Optional, если сенсор не найден.
     */
    Optional<Sensor> findByName(String name);
}
