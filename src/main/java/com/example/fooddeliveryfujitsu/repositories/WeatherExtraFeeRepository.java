package com.example.fooddeliveryfujitsu.repositories;

import com.example.fooddeliveryfujitsu.models.WeatherExtraFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeatherExtraFeeRepository extends JpaRepository<WeatherExtraFee, Long> {

    @Query("SELECT w FROM WeatherExtraFee w WHERE w.feeType = 'AIR_TEMP' AND w.vehicleType = :vehicleType " +
            "AND :temperature >= w.minValue AND :temperature < w.maxValue " +
            "AND w.validFrom <= :dateTime AND (w.validTo IS NULL OR w.validTo > :dateTime) " +
            "ORDER BY w.validFrom DESC")
    Optional<WeatherExtraFee> findActiveAirTempRuleForVehicle(
            @Param("vehicleType") String vehicleType,
            @Param("temperature") Double temperature,
            @Param("dateTime") LocalDateTime dateTime);

    @Query("SELECT w FROM WeatherExtraFee w WHERE w.feeType = 'WIND_SPEED' AND w.vehicleType = :vehicleType " +
            "AND :windSpeed >= w.minValue AND :windSpeed < w.maxValue " +
            "AND w.validFrom <= :dateTime AND (w.validTo IS NULL OR w.validTo > :dateTime) " +
            "ORDER BY w.validFrom DESC")
    Optional<WeatherExtraFee> findActiveWindSpeedRuleForVehicle(
            @Param("vehicleType") String vehicleType,
            @Param("windSpeed") Double windSpeed,
            @Param("dateTime") LocalDateTime dateTime);

    @Query("SELECT w FROM WeatherExtraFee w WHERE w.feeType = 'WEATHER_PHENOMENON' AND w.vehicleType = :vehicleType " +
            "AND w.phenomenonCategory = :phenomenonCategory " +
            "AND w.validFrom <= :dateTime AND (w.validTo IS NULL OR w.validTo > :dateTime) " +
            "ORDER BY w.validFrom DESC")
    Optional<WeatherExtraFee> findActivePhenomenonRuleForVehicle(
            @Param("vehicleType") String vehicleType,
            @Param("phenomenonCategory") String phenomenonCategory,
            @Param("dateTime") LocalDateTime dateTime);

    List<WeatherExtraFee> findByFeeTypeOrderByValidFromDesc(String feeType);
}
