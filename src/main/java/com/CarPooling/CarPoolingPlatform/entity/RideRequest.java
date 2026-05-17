package com.CarPooling.CarPoolingPlatform.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RideRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String source;

    private String destination;

    private LocalDateTime requestTime;

    private String status;

    private Double pickupLat;
    private Double pickupLng;

    private Double dropLat;
    private Double dropLng;

    @ManyToOne
    @JoinColumn(name = "passenger_id")
    private User passenger;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private User driver;

    @Version
    private int version = 0;

    double sourceLat;
    double sourceLng;
    double destLat;
    double destLng;


}
