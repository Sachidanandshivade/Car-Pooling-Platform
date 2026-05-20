package com.CarPooling.CarPoolingPlatform.controller;

import com.CarPooling.CarPoolingPlatform.dto.ApiResponse;
import com.CarPooling.CarPoolingPlatform.entity.Ride;
import com.CarPooling.CarPoolingPlatform.service.RideService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/rides")
@RequiredArgsConstructor
public class RideController {

    private final RideService rideService;

    @GetMapping
    public ResponseEntity<ApiResponse> getAllRides() {
        List<Ride> rides = rideService.getAllRides();
        return ResponseEntity.ok(new ApiResponse("Rides fetched", rides, LocalDateTime.now()));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse> searchRides(
            @RequestParam String source,
            @RequestParam String destination) {
        List<Ride> rides = rideService.searchRides(source, destination);
        return ResponseEntity.ok(new ApiResponse("Search results", rides, LocalDateTime.now()));
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<ApiResponse> startRide(@PathVariable Long id, Authentication auth) {
        String message = rideService.startRide(id, auth.getName());
        return ResponseEntity.ok(new ApiResponse(message, null, LocalDateTime.now()));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<ApiResponse> completeRide(@PathVariable Long id, Authentication auth) {
        String message = rideService.completeRide(id, auth.getName());
        return ResponseEntity.ok(new ApiResponse(message, null, LocalDateTime.now()));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse> cancelRide(@PathVariable Long id, Authentication auth) {
        String message = rideService.cancelRide(id, auth.getName());
        return ResponseEntity.ok(new ApiResponse(message, null, LocalDateTime.now()));
    }
}
