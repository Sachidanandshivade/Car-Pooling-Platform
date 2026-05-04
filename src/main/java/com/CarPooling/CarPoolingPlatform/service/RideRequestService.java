package com.CarPooling.CarPoolingPlatform.service;

<<<<<<< HEAD
import com.CarPooling.CarPoolingPlatform.dto.CreateRideRequestDto;
=======
import com.CarPooling.CarPoolingPlatform.dto.CreateRideRequest;
import com.CarPooling.CarPoolingPlatform.dto.createRideRequestDto;
>>>>>>> f23408174ba710020cb531aa74c34512142e947a
import com.CarPooling.CarPoolingPlatform.entity.Ride;
import com.CarPooling.CarPoolingPlatform.entity.RideRequest;
import com.CarPooling.CarPoolingPlatform.entity.User;
import com.CarPooling.CarPoolingPlatform.repository.RideRepository;
<<<<<<< HEAD
import com.CarPooling.CarPoolingPlatform.repository.RideRequestRepositorys;
=======
import com.CarPooling.CarPoolingPlatform.repository.RideRequestRepository;
>>>>>>> f23408174ba710020cb531aa74c34512142e947a
import com.CarPooling.CarPoolingPlatform.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RideRequestService {

<<<<<<< HEAD
    private final RideRequestRepositorys rideRequestRepository;
    private final UserRepository userRepository;
    private final RideRepository rideRepository;

    public String createRequest(@org.jetbrains.annotations.UnknownNullability CreateRideRequestDto dto, String email){
=======
    private final RideRequestRepository rideRequestRepository;
    private final UserRepository userRepository;
    private final RideRepository rideRepository;

    public String createRequest(createRideRequestDto dto, String email){
>>>>>>> f23408174ba710020cb531aa74c34512142e947a
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

    @Transactional
    public String acceptRequest(Long requestId, String email){
        try {
            User driver = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Driver not found"));

            RideRequest request = rideRequestRepository.findById(requestId)
                    .orElseThrow(() -> new RuntimeException("Request not found"));

<<<<<<< HEAD
            if (!request.getStatus().equals("PENDING")) {
=======
            if (!request.getStatus().equals("Pending")) {
>>>>>>> f23408174ba710020cb531aa74c34512142e947a
                throw new RuntimeException(("Request already accepted"));
            }

            request.setDriver(driver);
<<<<<<< HEAD
            request.setStatus("ACCEPTED");
=======
            request.setStatus("Accepted");
>>>>>>> f23408174ba710020cb531aa74c34512142e947a

            rideRequestRepository.save(request);
            Ride ride = Ride.builder()
                    .source(request.getSource())
                    .destination(request.getDestination())
                    .departureTime(request.getRequestTime())
                    .availableSeats(4) // default for now
                    .price(500) // default
                    .driver(driver)
                    .build();

            rideRepository.save(ride);

            return "Request accepted and ride created";
        }
        catch(ObjectOptimisticLockingFailureException e){
            throw new RuntimeException("Another driver accepted this request");
        }

    }
    public List<RideRequest> getPendingRequestsBySource(String source) {
        return rideRequestRepository.findByStatusAndSourceIgnoreCase("PENDING",source);
    }

}
