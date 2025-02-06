package com.example.demo.util;

import com.example.demo.entity.Sensor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import com.example.demo.service.implementation.SensorService;

@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class SensorValidator implements Validator {

    // Сервис для работы с сенсорами
    private final SensorService sensorService;

    /**
     * Проверяет, поддерживает ли валидатор объект класса Sensor.
     * @param clazz Класс, для которого проверяется поддержка
     * @return true, если валидатор поддерживает этот класс, иначе false
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return Sensor.class.equals(clazz);
    }

    /**
     * Выполняет валидацию объекта Sensor.
     * Проверяет, существует ли уже сенсор с таким же именем.
     * @param target Объект, который нужно проверить
     * @param errors Объект для хранения ошибок валидации
     */
    @Override
    public void validate(Object target, Errors errors) {
        Sensor sensor = (Sensor) target;

        // Проверка, существует ли уже сенсор с таким именем в базе данных
        if (sensorService.getSensorByName(sensor.getName()).isPresent()) {
            // Добавление ошибки, если сенсор с таким именем уже существует
            errors.rejectValue("name", "", "Сенсор с именем: " + sensor.getName() + " уже существует");
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
