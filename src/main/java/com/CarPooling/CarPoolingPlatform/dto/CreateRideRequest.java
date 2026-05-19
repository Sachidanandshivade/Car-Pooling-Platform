package com.CarPooling.CarPoolingPlatform.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateRideRequest {
    private String source;
    private String destination;
    private LocalDateTime departureTime;
    private int availableSeats;
    private double fare;
}
