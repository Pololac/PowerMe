package com.powerme.repository;

import com.powerme.entity.ChargingStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ChargingStationRepository extends JpaRepository<ChargingStation, Long> {

    @Query("""
        select count(cs)
        from ChargingStation cs
        where cs.chargingLocation.id = :locationId
    """)
    int countByLocationId(Long locationId);
}
