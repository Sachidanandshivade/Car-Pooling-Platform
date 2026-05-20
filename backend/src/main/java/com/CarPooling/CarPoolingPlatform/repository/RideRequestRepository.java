package com.CarPooling.CarPoolingPlatform.repository;

import com.CarPooling.CarPoolingPlatform.entity.RideRequest;
import com.CarPooling.CarPoolingPlatform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RideRequestRepository extends JpaRepository<RideRequest, Long> {
    List<RideRequest> findByStatusAndSourceIgnoreCase(String status, String source);
    List<RideRequest> findByPassenger(User passenger);
}