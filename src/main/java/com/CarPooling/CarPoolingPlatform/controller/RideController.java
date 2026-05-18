package com.CarPooling.CarPoolingPlatform.controller;

import com.CarPooling.CarPoolingPlatform.dto.ApiResponse;
import com.CarPooling.CarPoolingPlatform.service.RideService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/rides")
@RequiredArgsConstructor
public class RideController {

    private final RideService rideService;

    @PutMapping("/{id}/start")
    public ResponseEntity<ApiResponse> startRide(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();

        String message = rideService.startRide(id, email);

        ApiResponse response = new ApiResponse(
                message,
                null,
                LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<ApiResponse> completeRide(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();

        String message = rideService.completeRide(id, email);

        ApiResponse response = new ApiResponse(
                message,
                null,
                LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse> cancelRide(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();

        String message = rideService.cancelRide(id, email);

        ApiResponse response = new ApiResponse(
                message,
                null,
                LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }
}