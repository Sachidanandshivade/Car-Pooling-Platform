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
            if (!request.getStatus().equals("PENDING")) {
                throw new RuntimeException("Already accepted");
            }
            List<Ride> rides = rideRepository.findAll();

            for (Ride ride : rides) {

                List<double[]> route = locationService.getRoute(
                        ride.getSourceLat(), ride.getSourceLng(),
                        ride.getDestLat(), ride.getDestLng()
                );

                boolean pickupMatch = locationService.isNearRoute(
                        request.getSourceLat(), request.getSourceLng(), route);

                boolean dropMatch = locationService.isNearRoute(
                        request.getDestLat(), request.getDestLng(), route);

                if (pickupMatch && dropMatch && ride.getAvailableSeats() > 0) {

                    ride.setAvailableSeats(ride.getAvailableSeats() - 1);
                    rideRepository.save(ride);

                    request.setStatus("MATCHED");
                    request.setDriver(ride.getDriver());
                    rideRequestRepository.save(request);

                    return "Smart matched ride";
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