package com.example.demo.util;

import com.example.demo.entity.Measurements;
import com.example.demo.service.implementation.MeasurementsService;
import com.example.demo.service.implementation.SensorService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class MeasurementsValidator implements Validator {

    // Сервис для работы с сенсорами
    private final SensorService sensorService;

    // Сервис для работы с измерениями
    private final MeasurementsService measurementsService;

    /**
     * Проверяет, поддерживает ли валидатор объект класса Measurements.
     * @param clazz Класс, для которого проверяется поддержка
     * @return true, если валидатор поддерживает этот класс, иначе false
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return Measurements.class.equals(clazz);
    }

    /**
     * Выполняет валидацию объекта Measurements.
     * @param target Объект, который нужно проверить
     * @param errors Объект для хранения ошибок валидации
     */
    @Override
    public void validate(Object target, Errors errors) {
        Measurements measurements = (Measurements) target;

        // Дополняем измерения дополнительными данными
        measurementsService.enrichMeasurements(measurements);

        // Если сенсор не установлен, прекращаем валидацию
        if (measurements.getSensor() == null) {
            return;
        }

        // Проверка существования сенсора с указанным именем
        if (sensorService.getSensorByName(measurements.getSensor().getName()).isEmpty()) {
            // Выводим информацию о сенсоре, если он не найден в базе данных
            System.out.println(sensorService.getSensorByName(measurements.getSensor().getName()));
            // Добавляем ошибку валидации
            errors.rejectValue("measurements", "", " Сенсор для этого измерения не существует в базе данных");
        }
    }

    /**
     * Стандартная реализация метода validateObject, не используется в текущей реализации.
     * @param target Объект, который нужно проверить
     * @return Ошибки валидации
     */
    @Override
    public Errors validateObject(Object target) {
        return Validator.super.validateObject(target);
    }
}
