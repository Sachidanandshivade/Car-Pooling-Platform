package com.CarPooling.CarPoolingPlatform.controller;

import com.CarPooling.CarPoolingPlatform.service.RideService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rides")
@RequiredArgsConstructor
public class RideController {

    private final RideService rideService;
    @PutMapping("/{id}/start")
    public String startRide(@PathVariable Long id,
                            Authentication authentication) {

        String email = authentication.getName();

        return rideService.startRide(id, email);
    }
    @PutMapping("/{id}/complete")
    public String completeRide(@PathVariable Long id,
                               Authentication authentication) {

        String email = authentication.getName();

        return rideService.completeRide(id, email);
    }
    @PutMapping("/{id}/cancel")
    public String cancelRide(@PathVariable Long id,
                             Authentication authentication) {

        String email = authentication.getName();

        return rideService.cancelRide(id, email);
    }
}
