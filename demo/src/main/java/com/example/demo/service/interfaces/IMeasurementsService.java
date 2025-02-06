package com.example.demo.service.interfaces;

import com.example.demo.entity.Measurements;

import java.util.List;

public interface IMeasurementsService {
    List<Measurements> getAllMeasurements();

    void save(Measurements measurements);

    void enrichMeasurements(Measurements measurements);
}
