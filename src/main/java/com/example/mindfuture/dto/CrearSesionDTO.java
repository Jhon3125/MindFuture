package com.example.mindfuture.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CrearSesionDTO {
    private LocalDate date;
    private LocalTime time;
    private Integer therapistId;
    private Integer duration;
}
