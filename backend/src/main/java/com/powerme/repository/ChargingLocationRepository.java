package com.powerme.repository;

import com.powerme.entity.ChargingLocation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChargingLocationRepository extends JpaRepository<ChargingLocation, Long> {

    /**
     * * Crée un rectangle et vérifie que le point est dedans
     */
    @Query(
        nativeQuery = true,
        value = """
                    SELECT cl.id
                    FROM charging_location cl
                    WHERE ST_Within(
                        cl.location::geometry,
                        ST_MakeEnvelope(
                            :west, :south, :east, :north, 4326
                        )
                    )
                """
    )
    List<Long> findIdsInBounds(
            @Param("north") double north,
            @Param("south") double south,
            @Param("east") double east,
            @Param("west") double west
    );


    @Query("""
                SELECT cl
                FROM ChargingLocation cl
                JOIN FETCH cl.address
                WHERE cl.id IN :ids
            """)
    List<ChargingLocation> findAllWithAddressByIdIn(
            @Param("ids") List<Long> ids
    );


    @Query("""
                SELECT cl
                FROM ChargingLocation cl
                JOIN FETCH cl.address
                WHERE cl.id = :id
            """)
    Optional<ChargingLocation> findByIdWithAddress(@Param("id") Long id);

    @Query("""
                select cl
                from ChargingLocation cl
                join fetch cl.address
                left join fetch cl.chargingStations
                where cl.id = :id
            """)
    Optional<ChargingLocation> findByIdWithAddressAndStations(@Param("id") Long id);
}
