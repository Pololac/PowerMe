package com.powerme.repository;

import com.powerme.entity.Booking;
import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("""
        select count(b) > 0
        from Booking b
        where b.chargingStation.id = :stationId
          and :now between b.startTime and b.endTime
    """)
    boolean existsActiveBookingAt(
            @Param("stationId") Long stationId,
            @Param("now") Instant now
    );
}
