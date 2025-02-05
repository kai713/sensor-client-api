package com.example.demo.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SensorDTO {
    @NotEmpty(message = "Название сенсора не может быть пустым")
    @Size(min = 3, max = 30, message = "Значение должно быть между 3 до 30")
    private String name;
}