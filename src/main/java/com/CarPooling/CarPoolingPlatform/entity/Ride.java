package com.CarPooling.CarPoolingPlatform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String source;
    private String destination;

    private LocalDateTime departureTime;

    private int availableSeats;
    private double price;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private User driver;

    double sourceLat;
    double sourceLng;
    double destLat;
    double destLng;
}
