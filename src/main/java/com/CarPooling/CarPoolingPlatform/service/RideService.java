package com.CarPooling.CarPoolingPlatform.service;

import com.CarPooling.CarPoolingPlatform.entity.Ride;
import com.CarPooling.CarPoolingPlatform.entity.User;
import com.CarPooling.CarPoolingPlatform.repository.RideRepository;
import com.CarPooling.CarPoolingPlatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RideService {

    private final RideRepository rideRepository;
    private final UserRepository userRepository;

    public String startRide(Long rideId, String email) {

        // 1️⃣ Get driver
        User driver = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        // 2️⃣ Get ride
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        // 3️⃣ Check if this driver owns the ride
        if (!ride.getDriver().getId().equals(driver.getId())) {
            throw new RuntimeException("You are not assigned to this ride");
        }

        // 4️⃣ Only ACCEPTED rides can be started
        if (!ride.getStatus().equals("ACCEPTED")) {
            throw new RuntimeException("Ride cannot be started");
        }

        // 5️⃣ Update ride status
        ride.setStatus("STARTED");
        ride.setStartTime(LocalDateTime.now());

        // 6️⃣ Save
        rideRepository.save(ride);

        return "Ride started successfully";
    }
    public String completeRide(Long rideId, String email) {

        // 1️⃣ Get driver
        User driver = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        // 2️⃣ Get ride
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        // 3️⃣ Verify ownership
        if (!ride.getDriver().getId().equals(driver.getId())) {
            throw new RuntimeException("You are not assigned to this ride");
        }

        // 4️⃣ Only STARTED rides can be completed
        if (!ride.getStatus().equals("STARTED")) {
            throw new RuntimeException("Ride is not in progress");
        }

        // 5️⃣ Update ride
        ride.setStatus("COMPLETED");
        ride.setEndTime(LocalDateTime.now());

        // 6️⃣ Driver becomes available again
        driver.setAvailable(true);

        // 7️⃣ Save
        rideRepository.save(ride);
        userRepository.save(driver);

        return "Ride completed successfully";
    }
    public String cancelRide(Long rideId, String email) {

        // 1️⃣ Get user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2️⃣ Get ride
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        // 3️⃣ Only assigned driver or passenger can cancel
        boolean isDriver = ride.getDriver().getId().equals(user.getId());
        boolean isPassenger = ride.getPassenger().getId().equals(user.getId());

        if (!isDriver && !isPassenger) {
            throw new RuntimeException("You cannot cancel this ride");
        }

        // 4️⃣ Prevent cancelling completed rides
        if (ride.getStatus().equals("COMPLETED")) {
            throw new RuntimeException("Completed ride cannot be cancelled");
        }

        // 5️⃣ Update ride
        ride.setStatus("CANCELLED");

        // 6️⃣ Driver becomes available again
        User driver = ride.getDriver();
        driver.setAvailable(true);

        // 7️⃣ Save
        rideRepository.save(ride);
        userRepository.save(driver);

        return "Ride cancelled successfully";
    }
}