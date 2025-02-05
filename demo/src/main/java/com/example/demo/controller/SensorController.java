package com.example.demo.controller;

import com.example.demo.entity.Sensor;
import com.example.demo.service.SensorService;
import com.example.demo.exception.SensorErrorResponse;
import com.example.demo.exception.SensorNotCreatedException;
import com.example.demo.util.SensorValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import com.example.demo.dto.SensorDTO;

@RestController
@RequestMapping("/sensors")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Tag(name = "Measurements", description = "Управление cенсорами")
public class SensorController {

    private final SensorService sensorService;
    private final ModelMapper modelMapper;
    private final SensorValidator sensorValidator;


    @PostMapping("/add")
    @Operation(
            summary = "Добавление сенсора",
            description = "Добавляет новый сенсор",
            parameters = {
                    @Parameter(name = "name", description = "Название сенсора не может быть пустым, от 3 до 30 символов в названии", required = true, example = "Sensor 1"),
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Сенсор успешно добавлен"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации входных данных")
    })
    public ResponseEntity<HttpStatus> save(@RequestBody @Valid SensorDTO sensorDTO, BindingResult result) {
        sensorValidator.validate(convertToSensor(sensorDTO), result);
        if (result.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            for (ObjectError error : result.getAllErrors()) {
                errors.append(" - ").append(error.getDefaultMessage()).append(";");
            }
            throw new SensorNotCreatedException(errors.toString());
        }
        sensorService.save(convertToSensor(sensorDTO));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ExceptionHandler
    public ResponseEntity<SensorErrorResponse> handleException(SensorNotCreatedException ex) {
        SensorErrorResponse sensorErrorResponse = new SensorErrorResponse(
                ex.getMessage(),
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(sensorErrorResponse, HttpStatus.BAD_REQUEST);
    }

    private Sensor convertToSensor(SensorDTO sensorDTO) {
        return modelMapper.map(sensorDTO, Sensor.class);
    }
}