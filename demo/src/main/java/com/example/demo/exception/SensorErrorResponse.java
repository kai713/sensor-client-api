package com.example.demo.exception;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SensorErrorResponse {
    private String message;
    private long timestamp;
}
