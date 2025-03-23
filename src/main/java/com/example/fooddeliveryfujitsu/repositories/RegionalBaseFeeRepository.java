package com.example.fooddeliveryfujitsu.repositories;

import com.example.fooddeliveryfujitsu.models.RegionalBaseFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RegionalBaseFeeRepository extends JpaRepository<RegionalBaseFee, Long> {

    @Query("SELECT r FROM RegionalBaseFee r WHERE r.city = :city AND r.vehicleType = :vehicleType " +
            "AND r.validFrom <= :dateTime AND (r.validTo IS NULL OR r.validTo > :dateTime) " +
            "ORDER BY r.validFrom DESC")
    Optional<RegionalBaseFee> findActiveRuleForCityAndVehicle(
            @Param("city") String city,
            @Param("vehicleType") String vehicleType,
            @Param("dateTime") LocalDateTime dateTime);

    List<RegionalBaseFee> findByCityAndVehicleTypeOrderByValidFromDesc(String city, String vehicleType);
}



