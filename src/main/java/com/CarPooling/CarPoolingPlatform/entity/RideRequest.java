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

    private String status; // PENDING, ACCEPTED

    @ManyToOne
    @JoinColumn(name = "passenger_id")
    private User passenger;
}
