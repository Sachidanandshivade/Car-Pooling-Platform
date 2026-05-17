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

    private final LocationService locationService;
    private final RideRequestRepository rideRequestRepository;
    private final UserRepository userRepository;
    private final RideRepository rideRepository;

    public String createRequest(CreateRideRequestDto dto, String email) {

        double[] src = locationService.getCoordinates(dto.getSource());
        double[] dest = locationService.getCoordinates(dto.getDestination());



        User passenger = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        RideRequest request = RideRequest.builder()
                .source(dto.getSource())
                .destination(dto.getDestination())
                .requestTime(dto.getRequestTime())
                .status("PENDING")
                .passenger(passenger)
                .build();

        request.setSourceLat(src[0]);
        request.setSourceLng(src[1]);

        request.setDestLat(dest[0]);
        request.setDestLng(dest[1]);

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
            if (!request.getStatus().equals("REQUESTED")) {
                throw new RuntimeException("Already accepted");
            }
            if (!driver.isAvailable()) {
                throw new RuntimeException("Driver is already on a ride");
            }


            Ride newRide = Ride.builder()
                    .source(request.getSource())
                    .destination(request.getDestination())
                    .departureTime(request.getRequestTime())
                    .fare(500)
                    .status("ACCEPTED")
                    .driver(driver)
                    .passenger(request.getPassenger())
                    .build();

            rideRepository.save(newRide);
            driver.setAvailable(false);
            userRepository.save(driver);


        request.setStatus("ACCEPTED");
        request.setDriver(driver);
        rideRequestRepository.save(request);

        return "Ride accepted successfully";
    }  catch (ObjectOptimisticLockingFailureException e) {
        return "Someone else already accepted this request";
        }
        }

    public List<RideRequest> getPendingRequestsBySource(String source) {
        return rideRequestRepository.findByStatusAndSourceIgnoreCase("PENDING", source);
    }

}