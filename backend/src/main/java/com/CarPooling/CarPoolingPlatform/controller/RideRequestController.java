package com.CarPooling.CarPoolingPlatform.controller;

import com.CarPooling.CarPoolingPlatform.dto.ApiResponse;
import com.CarPooling.CarPoolingPlatform.dto.CreateRideRequestDto;
import com.CarPooling.CarPoolingPlatform.entity.RideRequest;
import com.CarPooling.CarPoolingPlatform.service.RideRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RideRequestController {

    private final RideRequestService rideRequestService;

    @PostMapping
    public ResponseEntity<ApiResponse> createRequest(
            @Valid @RequestBody CreateRideRequestDto dto,
            Authentication authentication) {
        String message = rideRequestService.createRequest(dto, authentication.getName());
        return ResponseEntity.ok(new ApiResponse(message, null, LocalDateTime.now()));
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<ApiResponse> acceptRequest(
            @PathVariable Long id,
            Authentication authentication) {
        String message = rideRequestService.acceptRequest(id, authentication.getName());
        return ResponseEntity.ok(new ApiResponse(message, null, LocalDateTime.now()));
    }

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse> getPendingRequests(@RequestParam String source) {
        List<RideRequest> requests = rideRequestService.getPendingRequestsBySource(source);
        return ResponseEntity.ok(new ApiResponse("Pending requests fetched", requests, LocalDateTime.now()));
    }

    // ── NEW: get current passenger's own requests ─────────────────────────────
    @GetMapping("/my")
    public ResponseEntity<ApiResponse> getMyRequests(Authentication authentication) {
        List<RideRequest> requests = rideRequestService.getMyRequests(authentication.getName());
        return ResponseEntity.ok(new ApiResponse("Your requests fetched", requests, LocalDateTime.now()));
    }
    
    @GetMapping("/estimate-fare")
public ResponseEntity<Double> estimateFare(@RequestParam String source,
                                            @RequestParam String destination) {

    double[] src = locationService.getCoordinates(source);
    double[] dest = locationService.getCoordinates(destination);

    double distance = locationService.distance(
            src[0], src[1],
            dest[0], dest[1]
    );

    double fare = 30 + (distance * 10); // same logic as backend

    return ResponseEntity.ok(fare);
}
}
