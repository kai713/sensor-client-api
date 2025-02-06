package com.example.demo;

import com.example.demo.entity.Measurements;
import com.example.demo.entity.Sensor;
import com.example.demo.exception.MeasureNotCreatedException;
import com.example.demo.repositories.MeasurementsRepository;
import com.example.demo.service.implementation.MeasurementsService;
import com.example.demo.service.implementation.SensorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeasurementsServiceTest {

    @Mock
    private SensorService sensorService;

    @Mock
    private MeasurementsRepository measurementsRepository;

    @InjectMocks
    private MeasurementsService measurementsService;

    private Measurements testMeasurement;
    private Sensor testSensor;

    @BeforeEach
    void setUp() {
        testSensor = new Sensor();
        testSensor.setName("TestSensor");

        testMeasurement = new Measurements();
        testMeasurement.setSensor(testSensor);
    }

    @Test
    void testGetAllMeasurements() {
        // Arrange
        Measurements measurement1 = new Measurements();
        Measurements measurement2 = new Measurements();
        List<Measurements> expectedList = Arrays.asList(measurement1, measurement2);

        when(measurementsRepository.findAll()).thenReturn(expectedList);

        // Act
        List<Measurements> result = measurementsService.getAllMeasurements();

        // Assert
        assertEquals(2, result.size());
        verify(measurementsRepository, times(1)).findAll();
    }

    @Test
    void testSave_ValidMeasurement() {
        // Arrange
        when(sensorService.getSensorByName("TestSensor")).thenReturn(Optional.of(testSensor));

        // Act
        measurementsService.save(testMeasurement);

        // Assert
        verify(measurementsRepository, times(1)).save(testMeasurement);
        assertEquals(testSensor, testMeasurement.getSensor()); // Убедимся, что сенсор установлен правильно
    }

    @Test
    void testEnrichMeasurements_SensorFound() {
        // Arrange
        when(sensorService.getSensorByName("TestSensor")).thenReturn(Optional.of(testSensor));

        // Act
        measurementsService.enrichMeasurements(testMeasurement);

        // Assert
        assertEquals(testSensor, testMeasurement.getSensor());
    }

    @Test
    void testEnrichMeasurements_SensorNotFound() {
        // Arrange
        when(sensorService.getSensorByName("TestSensor")).thenReturn(Optional.empty());

        // Act & Assert
        MeasureNotCreatedException exception = assertThrows(
                MeasureNotCreatedException.class,
                () -> measurementsService.enrichMeasurements(testMeasurement)
        );

        assertTrue(exception.getMessage().contains("Cенсор с названием: TestSensor не найден"));
    }
}
