package com.example.demo;


import com.example.demo.entity.Sensor;
import com.example.demo.repositories.SensorRepository;
import com.example.demo.service.implementation.SensorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SensorServiceTest {

    @Mock
    private SensorRepository sensorRepository;

    @InjectMocks
    private SensorService sensorService;

    private Sensor testSensor;

    @BeforeEach
    void setUp() {
        testSensor = new Sensor();
        testSensor.setName("TestSensor");
    }

    @Test
    void testGetSensorByName_SensorFound() {
        when(sensorRepository.findByName("TestSensor")).thenReturn(Optional.of(testSensor));

        Optional<Sensor> result = sensorService.getSensorByName("TestSensor");

        assertTrue(result.isPresent());
        assertEquals("TestSensor", result.get().getName());
        verify(sensorRepository, times(1)).findByName("TestSensor");
    }

    @Test
    void testGetSensorByName_SensorNotFound() {
        when(sensorRepository.findByName("UnknownSensor")).thenReturn(Optional.empty());

        Optional<Sensor> result = sensorService.getSensorByName("UnknownSensor");

        assertFalse(result.isPresent());
        verify(sensorRepository, times(1)).findByName("UnknownSensor");
    }

    @Test
    void testSaveSensor() {
        sensorService.save(testSensor);

        verify(sensorRepository, times(1)).save(testSensor);
    }
}

