package com.CarPooling.CarPoolingPlatform.controller;

import com.CarPooling.CarPoolingPlatform.dto.ApiResponse;
import com.CarPooling.CarPoolingPlatform.dto.CreateRideRequestDto;
import com.CarPooling.CarPoolingPlatform.entity.RideRequest;
import com.CarPooling.CarPoolingPlatform.service.RideRequestService;
import com.CarPooling.CarPoolingPlatform.service.LocationService;
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

     private final LocationService locationService;
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
public ResponseEntity<ApiResponse> estimateFare(
        @RequestParam String source,
        @RequestParam String destination) {

    double[] src = locationService.getCoordinates(source);
    double[] dest = locationService.getCoordinates(destination);

    // ✅ Use road distance instead of straight line
    List<double[]> route = locationService.getRoute(
            src[0], src[1], dest[0], dest[1]);
    double distance = locationService.roadDistance(route);

    // ✅ Sanity check — no Bangalore route should exceed 80 km
    if (distance > 80) {
        return ResponseEntity.badRequest().body(
                new ApiResponse(
                        "Locations seem to be outside Bangalore (distance: "
                        + String.format("%.1f", distance) + " km). " +
                        "Please enter valid Bangalore localities.",
                        null,
                        LocalDateTime.now()
                )
        );
    }

    double fare = Math.round((30 + distance * 10) * 100.0) / 100.0;

    return ResponseEntity.ok(
            new ApiResponse("Fare estimated successfully", fare, LocalDateTime.now()));
}
}
