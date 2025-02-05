package com.example.demo.controller;

import com.example.demo.dto.MeasurementsDTO;
import com.example.demo.dto.MeasurementsResponse;
import com.example.demo.service.MeasurementsService;
import com.example.demo.exception.MeasureNotCreatedException;
import com.example.demo.exception.MeasurementsErrorResponse;
import com.example.demo.util.MeasurementsValidator;
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
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import com.example.demo.entity.Measurements;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/measurements")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Tag(name = "Measurements", description = "Управление измерениями")
public class MeasurementController {

    private final MeasurementsService measurementsService;
    private final ModelMapper modelMapper;
    private final MeasurementsValidator measurementsValidator;


    @GetMapping
    @Operation(summary = "Получить все измерения", description = "Возвращает список всех измерений, зарегистрированных в системе")
    @ApiResponse(responseCode = "200", description = "Успешное получение списка измерений")
    public MeasurementsResponse getAll() {
        return new MeasurementsResponse(measurementsService.getAllMeasurements()
                .stream().map(this::convertToMeasurementsDTO).collect(Collectors.toList()));
    }

    @GetMapping("/rainyDaysCount")
    @Operation(summary = "Получить все дождливые дни", description = "Возвращает список всех дождливый дней, зарегистрированных в системе")
    @ApiResponse(responseCode = "200", description = "Успешное получение списка измерений")
    public Long getRainyDaysCount() {
        return measurementsService.getAllMeasurements().stream().filter(Measurements::isRaining).count();
    }

    @PostMapping("/add")
    @Operation(
            summary = "Добавление измерения",
            description = "Добавляет новое измерение с температурой в диапазоне от -100 до +100 градусов и статусом дождя",
            parameters = {
                    @Parameter(name = "value", description = "Температура измерения (-100 до +100) принимает double значение", required = true, example = "25.2"),
                    @Parameter(name = "isRaining", description = "Флаг, указывающий идет ли дождь, принимает только boolean значение, true или false", required = true, example = "true"),
                    @Parameter(name = "sensor", description = "Объект сенсора, к которому относится измерение", required = true)
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Измерение успешно добавлено"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации входных данных")
    })
    public ResponseEntity<HttpStatus> save(@Parameter(description = "DTO измерения с данными о температуре, осадках и сенсоре") @RequestBody @Valid MeasurementsDTO measurementsDTO, BindingResult bindingResult) {
        measurementsValidator.validate(convertToMeasurements(measurementsDTO), bindingResult);
        if (bindingResult.hasErrors()) {
            StringBuilder message = new StringBuilder();
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                message.append(" - ").append(fieldError.getDefaultMessage()).append(";");
            }
            throw new MeasureNotCreatedException(message.toString());
        }
        measurementsService.save(convertToMeasurements(measurementsDTO));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ExceptionHandler
    public ResponseEntity<MeasurementsErrorResponse> handleException(MeasureNotCreatedException ex) {
        MeasurementsErrorResponse errorResponse = new MeasurementsErrorResponse(
                ex.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    private Measurements convertToMeasurements(MeasurementsDTO measurementsDTO) {
        return modelMapper.map(measurementsDTO, Measurements.class);
    }

    private MeasurementsDTO convertToMeasurementsDTO(Measurements measurements) {
        return modelMapper.map(measurements, MeasurementsDTO.class);
    }
}
