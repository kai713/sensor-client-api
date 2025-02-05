package com.example.demo.service;

import com.example.demo.entity.Measurements;
import com.example.demo.entity.Sensor;
import com.example.demo.repositories.MeasurementsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.exception.MeasureNotCreatedException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class MeasurementsService {

    private final SensorService sensorService;
    private final MeasurementsRepository measurementsRepository;

    public List<Measurements> getAllMeasurements() {
        return measurementsRepository.findAll();
    }

    @Transactional
    public void save(Measurements measurements) {
        enrichMeasurements(measurements);
        measurementsRepository.save(measurements);
    }

    @Transactional
    public void enrichMeasurements(Measurements measurements) {
        Optional<Sensor> sensorOptional = sensorService.getSensorByName(measurements.getSensor().getName());

        if (sensorOptional.isPresent()) {
            measurements.setSensor(sensorOptional.get());
        } else {
            throw new MeasureNotCreatedException("Cенсор с названием: " + measurements.getSensor().getName() + "не найден");
        }
    }
}