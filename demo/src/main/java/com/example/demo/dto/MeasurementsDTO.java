package com.example.demo.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MeasurementsDTO {
    @Min(value = -100, message = "Значение должно быть больше -100")
    @Max(value = 100, message = "Значение должно быть меньше 100")
    @NotNull(message = "Значение не может быть пустым")
    private Double value;
    @NotNull(message = "Boolean значение погоды не может быть пустым")
    private Boolean isRaining;
    @NotNull(message = "Название сенсора не может быть пустым")
    SensorDTO sensor;
}
