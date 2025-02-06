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
    private final SensorService sensorService;
    private final MeasurementsService measurementsService;

    @Override
    public boolean supports(Class<?> clazz) {
        return Measurements.class.equals(clazz);
    }
    @Override
    public void validate(Object target, Errors errors) {
        Measurements measurements = (Measurements) target;
        measurementsService.enrichMeasurements(measurements);

        if (measurements.getSensor() == null) {
            return;
        }

        if (sensorService.getSensorByName(measurements.getSensor().getName()).isEmpty()) {
            System.out.println(sensorService.getSensorByName(measurements.getSensor().getName()));
            errors.rejectValue("measurements", "", " Сенсор для этого измерения не существует в базе данных");
        }
    }

    @Override
    public Errors validateObject(Object target) {
        return Validator.super.validateObject(target);
    }
}
