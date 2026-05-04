package com.CarPooling.CarPoolingPlatform.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateRideRequestDto {
    private String source;
    private String destination;
    private LocalDateTime requestTime;
}
