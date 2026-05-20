package com.CarPooling.CarPoolingPlatform.service;

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

    public String startRide(Long rideId, String email) {
        User driver = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        if (!ride.getDriver().getId().equals(driver.getId())) {
            throw new RuntimeException("You are not assigned to this ride");
        }

        if (!ride.getStatus().equals("ACCEPTED")) {
            throw new RuntimeException("Ride cannot be started — must be ACCEPTED first");
        }

        ride.setStatus("STARTED");
        ride.setStartTime(LocalDateTime.now());
        rideRepository.save(ride);

        return "Ride started successfully";
    }

    public String completeRide(Long rideId, String email) {
        User driver = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        if (!ride.getDriver().getId().equals(driver.getId())) {
            throw new RuntimeException("You are not assigned to this ride");
        }

        if (!ride.getStatus().equals("STARTED")) {
            throw new RuntimeException("Ride is not in progress");
        }

        ride.setStatus("COMPLETED");
        ride.setEndTime(LocalDateTime.now());

        driver.setAvailable(true);
        userRepository.save(driver);
        rideRepository.save(ride);

        return "Ride completed successfully";
    }

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
            throw new RuntimeException("You are not part of this ride");
        }

        if (ride.getStatus().equals("COMPLETED")) {
            throw new RuntimeException("Completed ride cannot be cancelled");
        }

        ride.setStatus("CANCELLED");

        if (ride.getDriver() != null) {
            User driver = ride.getDriver();
            driver.setAvailable(true);
            userRepository.save(driver);
        }

        rideRepository.save(ride);
        return "Ride cancelled successfully";
    }

    public List<Ride> getAllRides() {
        return rideRepository.findAll();
    }

    public List<Ride> searchRides(String source, String destination) {
        return rideRepository.searchPartial(source, destination);
    }
}
