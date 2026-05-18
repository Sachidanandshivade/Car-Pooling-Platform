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

        String email = authentication.getName();

        String message = rideRequestService.createRequest(dto, email);

        ApiResponse response = new ApiResponse(
                message,
                null,
                LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<ApiResponse> acceptRequest(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();

        String message = rideRequestService.acceptRequest(id, email);

        ApiResponse response = new ApiResponse(
                message,
                null,
                LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse> getFilteredRequests(
            @RequestParam String source) {

        List<RideRequest> requests =
                rideRequestService.getPendingRequestsBySource(source);

        ApiResponse response = new ApiResponse(
                "Pending requests fetched successfully",
                requests,
                LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }
}