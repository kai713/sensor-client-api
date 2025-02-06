package com.example.demo.service.implementation;

import com.example.demo.entity.Measurements;
import com.example.demo.entity.Sensor;
import com.example.demo.repositories.MeasurementsRepository;
import com.example.demo.service.interfaces.IMeasurementsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.exception.MeasureNotCreatedException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MeasurementsService implements IMeasurementsService {

    // Сервис для работы с сенсорами
    private final SensorService sensorService;

    // Репозиторий для работы с измерениями
    private final MeasurementsRepository measurementsRepository;

    /**
     * Получение списка всех измерений.
     * @return Список всех измерений
     */
    public List<Measurements> getAllMeasurements() {
        return measurementsRepository.findAll();
    }

    /**
     * Сохранение нового измерения.
     * Перед сохранением выполняется дополнение измерения сенсором.
     * @param measurements Объект измерения, который нужно сохранить
     */
    @Transactional
    public void save(Measurements measurements) {
        enrichMeasurements(measurements); // Дополнение измерения сенсором
        measurementsRepository.save(measurements); // Сохранение измерения
    }

    /**
     * Дополнение объекта Measurements сенсором на основе его имени.
     * Если сенсор с таким именем не найден, выбрасывается исключение.
     * @param measurements Объект измерения, который нужно дополнить
     */
    @Transactional
    public void enrichMeasurements(Measurements measurements) {
        Optional<Sensor> sensorOptional = sensorService.getSensorByName(measurements.getSensor().getName());

        // Если сенсор найден, устанавливаем его в измерение
        if (sensorOptional.isPresent()) {
            measurements.setSensor(sensorOptional.get());
        } else {
            // Если сенсор не найден, выбрасываем исключение
            throw new MeasureNotCreatedException("Cенсор с названием: " + measurements.getSensor().getName() + " не найден");
        }
    }
}
