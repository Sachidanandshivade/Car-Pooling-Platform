package com.CarPooling.CarPoolingPlatform.service;

import com.CarPooling.CarPoolingPlatform.dto.CreateRideRequest;
import com.CarPooling.CarPoolingPlatform.dto.createRideRequestDto;
import com.CarPooling.CarPoolingPlatform.entity.RideRequest;
import com.CarPooling.CarPoolingPlatform.entity.User;
import com.CarPooling.CarPoolingPlatform.repository.RideRequestRepository;
import com.CarPooling.CarPoolingPlatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RideRequestService {

    private final RideRequestRepository rideRequestRepository;
    private final UserRepository userRepository;

    public String createRequest(createRideRequestDto dto, String email){
        User passenger = userRepository.findByEmail(email)
                .orElseThrow(()->new RuntimeException("user not found"));

        RideRequest request = RideRequest.builder()
                .source(dto.getSource())
                .destination(dto.getDestination())
                .requestTime(dto.getRequestTime())
                .status("PENDING")
                .passenger(passenger)
                .build();

        rideRequestRepository.save(request);

        return "Ride request created successfully";
    }
}
