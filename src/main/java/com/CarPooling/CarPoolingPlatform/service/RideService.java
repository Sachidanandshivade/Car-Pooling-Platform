package com.CarPooling.CarPoolingPlatform.service;

import com.CarPooling.CarPoolingPlatform.dto.CreateRideRequest;
import com.CarPooling.CarPoolingPlatform.entity.Ride;
import com.CarPooling.CarPoolingPlatform.entity.User;
import com.CarPooling.CarPoolingPlatform.repository.RideRepository;
import com.CarPooling.CarPoolingPlatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RideService {
    private final RideRepository rideRepository;
    private final UserRepository userRepository;

    public String createRide(CreateRideRequest request, String email) {

        User driver = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));



        Ride ride = Ride.builder()
                .source(request.getSource())
                .destination(request.getDestination())
                .departureTime(request.getDepartureTime())
                .availableSeats(request.getAvailableSeats())
                .price(request.getPrice())
                .driver(driver)
                .build();

        rideRepository.save(ride);
        return "Ride created successfully";
    }
    public List<Ride> searchRides(String source,String destination){
        return rideRepository.searchPartial(source,destination);
    }

}
