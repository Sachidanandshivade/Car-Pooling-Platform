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
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RideRequestService {

      private final LocationService locationService;
    
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
                .sourceLat(src[0])
                .sourceLng(src[1])
                .destLat(dest[0])
                .destLng(dest[1])
                .build();

        rideRequestRepository.save(request);
        return "Ride request created successfully";
    }

    @Transactional
    public String acceptRequest(Long requestId, String email) {
        try {
            User driver = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Driver not found"));

            RideRequest request = rideRequestRepository.findById(requestId)
                    .orElseThrow(() -> new RuntimeException("Request not found"));

            if (!request.getStatus().equals("PENDING")) {
                throw new RuntimeException("Request is no longer available");
            }

            if (!driver.isAvailable()) {
                throw new RuntimeException("You are already on a ride");
            }
            
            double distance = locationService.distance(
        request.getSourceLat(),
        request.getSourceLng(),
        request.getDestLat(),
        request.getDestLng()
);
              System.out.println("SOURCE LAT LNG: " + request.getSourceLat() + " , " + request.getSourceLng());
System.out.println("DEST LAT LNG: " + request.getDestLat() + " , " + request.getDestLng());
double fare = BASE_FARE + (distance * PRICE_PER_KM);

            Ride newRide = Ride.builder()
                    .source(request.getSource())
                    .destination(request.getDestination())
                    .departureTime(request.getRequestTime())
                    .fare(fare)
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

        } catch (ObjectOptimisticLockingFailureException e) {
            return "Someone else already accepted this request";
        }
    }

    public List<RideRequest> getPendingRequestsBySource(String source) {
        return rideRequestRepository.findByStatusAndSourceIgnoreCase("PENDING", source);
    }
    public List<RideRequest> getMyRequests(String email) {
        User passenger = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return rideRequestRepository.findByPassenger(passenger);
    }
    
}
