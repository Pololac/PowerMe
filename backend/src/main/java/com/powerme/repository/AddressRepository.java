package com.powerme.repository;

import com.powerme.entity.Address;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    Optional<Address> findByStreetAddressAndPostalCodeAndCityAndCountry(
            String street,
            String postalCode,
            String city,
            String country
    );
}
