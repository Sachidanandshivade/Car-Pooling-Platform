package com.CarPooling.CarPoolingPlatform.service;

import com.CarPooling.CarPoolingPlatform.dto.CreateRideRequestDto;
import com.CarPooling.CarPoolingPlatform.entity.Ride;
import com.CarPooling.CarPoolingPlatform.entity.RideRequest;
import com.CarPooling.CarPoolingPlatform.entity.User;
import com.CarPooling.CarPoolingPlatform.repository.RideRepository;
import com.CarPooling.CarPoolingPlatform.repository.RideRequestRepository;
import com.CarPooling.CarPoolingPlatform.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RideRequestService {

    private final RideRequestRepository rideRequestRepository;
    private final UserRepository userRepository;
    private final RideRepository rideRepository;

    public String createRequest(CreateRideRequestDto dto, String email) {

        User passenger = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

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

    @Transactional
    public String acceptRequest(Long requestId, String email) {

        try {
            // 1️⃣ Get driver
            User driver = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Driver not found"));

            // 2️⃣ Get request
            RideRequest request = rideRequestRepository.findById(requestId)
                    .orElseThrow(() -> new RuntimeException("Request not found"));

            // 3️⃣ Prevent double accept
            if (!request.getStatus().equals("PENDING")) {
                throw new RuntimeException("Already accepted");
            }

        List<Ride> rides = rideRepository.findMatchingRides(
                request.getSource(),
                request.getDestination()
        );

        for (Ride ride : rides) {
            if (ride.getAvailableSeats() > 0) {

                // reduce seat
                ride.setAvailableSeats(ride.getAvailableSeats() - 1);
                rideRepository.save(ride);

                // update request
                request.setStatus("MATCHED");
                request.setDriver(driver);
                rideRequestRepository.save(request);

                return "Joined existing ride";
            }
        }

        Ride newRide = Ride.builder()
                .source(request.getSource())
                .destination(request.getDestination())
                .departureTime(request.getRequestTime())
                .availableSeats(4)
                .price(500)
                .driver(driver)
                .build();

        rideRepository.save(newRide);

        request.setStatus("ACCEPTED");
        request.setDriver(driver);
        rideRequestRepository.save(request);

        return "New ride created";
    }  catch (ObjectOptimisticLockingFailureException e) {
        return "Someone else already accepted this request";
        }
        }

    public List<RideRequest> getPendingRequestsBySource(String source) {
        return rideRequestRepository.findByStatusAndSourceIgnoreCase("PENDING", source);
    }
}