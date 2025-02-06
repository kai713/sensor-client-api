package com.example.demo.service.implementation;

import com.example.demo.entity.Sensor;
import com.example.demo.repositories.SensorRepository;
import com.example.demo.service.interfaces.ISensorService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class SensorService implements ISensorService {

    // Репозиторий для работы с сенсорами
    private final SensorRepository sensorRepository;

    /**
     * Находит сенсор по имени.
     * @param name Имя сенсора
     * @return Опционально возвращает найденный сенсор
     */
    public Optional<Sensor> getSensorByName(String name) {
        // Поиск сенсора в репозитории по имени
        Optional<Sensor> sensor = sensorRepository.findByName(name);
        return sensor;
    }

    /**
     * Сохраняет новый или обновляет существующий сенсор в базе данных.
     * @param sensor Сенсор, который нужно сохранить
     */
    @Transactional
    public void save(Sensor sensor) {
        // Сохранение сенсора в базе данных
        sensorRepository.save(sensor);
    }
}
