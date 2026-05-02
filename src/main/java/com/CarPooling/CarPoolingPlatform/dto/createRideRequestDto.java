package com.CarPooling.CarPoolingPlatform.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class createRideRequestDto {
    private String source;
    private String destination;
    private LocalDateTime requestTime;
}
