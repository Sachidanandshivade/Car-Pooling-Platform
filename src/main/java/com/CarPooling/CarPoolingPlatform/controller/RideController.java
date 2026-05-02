package com.CarPooling.CarPoolingPlatform.controller;


import com.CarPooling.CarPoolingPlatform.dto.CreateRideRequest;
import com.CarPooling.CarPoolingPlatform.entity.Ride;
import com.CarPooling.CarPoolingPlatform.security.JwtUtil;
import com.CarPooling.CarPoolingPlatform.service.RideService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rides")
@RequiredArgsConstructor
public class RideController {

    private final RideService rideService;
    private final JwtUtil jwtUtil;

    @PostMapping
    public String createRide(
            @RequestBody CreateRideRequest request,
            Authentication authentication) {

        String email = authentication.getName(); // 🔥 comes from JWT

        return rideService.createRide(request, email);
    }

    @GetMapping("/search")
    public List<Ride> searchRides(@RequestParam String source, @RequestParam String destination){
        return rideService.searchRides(source,destination);
    }

}
