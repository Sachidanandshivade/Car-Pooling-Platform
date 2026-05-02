package com.CarPooling.CarPoolingPlatform.controller;

import com.CarPooling.CarPoolingPlatform.dto.CreateRideRequest;
import com.CarPooling.CarPoolingPlatform.dto.createRideRequestDto;
import com.CarPooling.CarPoolingPlatform.service.RideRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RideRequestController {
    private final RideRequestService rideRequestService;

    @PostMapping
    public String createRequest(@RequestBody createRideRequestDto dto, Authentication authentication){
        String email = authentication.getName();
        return rideRequestService.createRequest(dto,email);
    }

}
