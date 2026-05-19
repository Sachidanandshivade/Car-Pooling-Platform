package com.CarPooling.CarPoolingPlatform.service;

import com.CarPooling.CarPoolingPlatform.dto.CreateRideRequest;
import com.CarPooling.CarPoolingPlatform.entity.Ride;
import com.CarPooling.CarPoolingPlatform.entity.User;
import com.CarPooling.CarPoolingPlatform.repository.RideRepository;
import com.CarPooling.CarPoolingPlatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RideService {

    private final RideRepository rideRepository;
    private final UserRepository userRepository;


    // START RIDE
    public String startRide(Long rideId, String email) {

        User driver = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        // Verify driver ownership
        if (!ride.getDriver().getId().equals(driver.getId())) {
            throw new RuntimeException("You are not assigned to this ride");
        }

        // Only ACCEPTED rides can start
        if (!ride.getStatus().equals("ACCEPTED")) {
            throw new RuntimeException("Ride cannot be started");
        }

        ride.setStatus("STARTED");
        ride.setStartTime(LocalDateTime.now());

        rideRepository.save(ride);

        return "Ride started successfully";
    }

    // COMPLETE RIDE
    public String completeRide(Long rideId, String email) {

        User driver = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        // Verify driver ownership
        if (!ride.getDriver().getId().equals(driver.getId())) {
            throw new RuntimeException("You are not assigned to this ride");
        }

        // Only STARTED rides can complete
        if (!ride.getStatus().equals("STARTED")) {
            throw new RuntimeException("Ride is not in progress");
        }

        ride.setStatus("COMPLETED");
        ride.setEndTime(LocalDateTime.now());

        // Driver becomes available again
        driver.setAvailable(true);

        rideRepository.save(ride);
        userRepository.save(driver);

        return "Ride completed successfully";
    }

    // CANCEL RIDE
    public String cancelRide(Long rideId, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        boolean isDriver = ride.getDriver() != null &&
                ride.getDriver().getId().equals(user.getId());

        boolean isPassenger = ride.getPassenger() != null &&
                ride.getPassenger().getId().equals(user.getId());

        if (!isDriver && !isPassenger) {
            throw new RuntimeException("You cannot cancel this ride");
        }

        if (ride.getStatus().equals("COMPLETED")) {
            throw new RuntimeException("Completed ride cannot be cancelled");
        }

        ride.setStatus("CANCELLED");

        // Make driver available again
        if (ride.getDriver() != null) {
            User driver = ride.getDriver();
            driver.setAvailable(true);
            userRepository.save(driver);
        }

        rideRepository.save(ride);

        return "Ride cancelled successfully";
    }

    // GET ALL RIDES
    public List<Ride> getAllRides() {
        return rideRepository.findAll();
    }
}