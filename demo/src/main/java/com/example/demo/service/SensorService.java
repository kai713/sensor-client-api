package com.example.demo.service;

import com.example.demo.entity.Sensor;
import com.example.demo.repositories.SensorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class SensorService {

    private final SensorRepository sensorRepository;

    public Optional<Sensor> getSensorByName(String name) {
        Optional<Sensor> sensor = sensorRepository.findByName(name);
        return sensor;
    }

    @Transactional
    public void save(Sensor sensor) {
        sensorRepository.save(sensor);
    }
}
