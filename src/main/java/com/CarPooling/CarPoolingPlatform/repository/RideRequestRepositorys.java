package com.CarPooling.CarPoolingPlatform.repository;

import com.CarPooling.CarPoolingPlatform.entity.RideRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RideRequestRepositorys extends JpaRepository<RideRequest , Long> {
    List<RideRequest> findByStatusAndSourceIgnoreCase(String status, String source);
}

