package com.example.demo.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MeasurementsResponse {
    List<MeasurementsDTO> list;
}
