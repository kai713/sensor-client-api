package com.example.demo.service.interfaces;

import com.example.demo.entity.Sensor;

import java.util.Optional;

public interface ISensorService {

    Optional<Sensor> getSensorByName(String name);

    void save(Sensor sensor);
}
