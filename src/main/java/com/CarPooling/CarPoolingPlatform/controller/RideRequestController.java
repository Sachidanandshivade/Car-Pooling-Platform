package com.CarPooling.CarPoolingPlatform.controller;

import com.CarPooling.CarPoolingPlatform.dto.CreateRideRequestDto;
import com.CarPooling.CarPoolingPlatform.entity.RideRequest;
import com.CarPooling.CarPoolingPlatform.service.RideRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RideRequestController {

    private final RideRequestService rideRequestService;

    @PostMapping
    public String createRequest(@RequestBody CreateRideRequestDto dto,
                                Authentication authentication) {

        String email = authentication.getName();
        return rideRequestService.createRequest(dto, email);

    }

    @PostMapping("/{id}/accept")
    public String acceptRequest(@PathVariable Long id,
                                Authentication authentication) {

        String email = authentication.getName();
        return rideRequestService.acceptRequest(id, email);
    }

    @GetMapping("/pending")
    public List<RideRequest> getFilteredRequests(@RequestParam String source) {
        return rideRequestService.getPendingRequestsBySource(source);
    }
}