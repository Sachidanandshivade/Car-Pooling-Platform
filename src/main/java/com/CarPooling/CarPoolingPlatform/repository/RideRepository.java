package com.CarPooling.CarPoolingPlatform.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.CarPooling.CarPoolingPlatform.entity.Ride;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RideRepository extends JpaRepository<Ride,  Long> {
    List<Ride> findBySourceIgnoreCaseAndDestinationIgnoreCase(String source, String destination);

    @Query("SELECT r FROM Ride r WHERE LOWER(r.source) LIKE LOWER(CONCAT('%', :source, '%')) AND LOWER(r.destination) LIKE LOWER(CONCAT('%', :destination, '%'))")
    List<Ride> searchPartial(@Param("source") String source,
                             @Param("destination") String destination);

    @Query("SELECT r FROM Ride r WHERE r.source = :source AND r.destination = :destination")
    List<Ride> findMatchingRides(String source, String destination);
}
