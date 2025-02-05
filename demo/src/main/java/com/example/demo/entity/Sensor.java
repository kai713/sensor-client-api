package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sensor")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Sensor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name", unique = true)
    @NotEmpty(message = "Название сенсора не может быть пустым")
    @Size(min = 3, max = 30, message = "Значение должно быть между 3 до 30")
    private String name;

    @Column(name = "created_at")
    @NotNull
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
