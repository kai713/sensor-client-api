package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Measurements")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Measurements {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "value")
    @Min(value = -100, message = "Значение должно быть больше -100")
    @Max(value = 100, message = "Значение должно быть меньше 100")
    @NotNull(message = "Значение не может быть пустым")
    private Double value;

    @Column(name = "raining")
    @NotNull(message = "Boolean значение погоды не может быть пустым")
    private Boolean isRaining;

    @Column(name = "created_at")
    @NotNull
    private LocalDateTime createdAt;

    @NotNull(message = "Название сенсора не может быть пустым")
    @JoinColumn(name = "sensor", referencedColumnName = "name")
    @ManyToOne
    private Sensor sensor;

    public Boolean isRaining() {
        return isRaining;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
