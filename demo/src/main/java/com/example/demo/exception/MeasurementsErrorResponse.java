package com.example.demo.exception;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MeasurementsErrorResponse {
    private String message;
    private long timestamp;
}