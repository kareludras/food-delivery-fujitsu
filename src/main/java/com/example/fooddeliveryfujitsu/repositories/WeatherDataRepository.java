package com.example.fooddeliveryfujitsu.repositories;

import com.example.fooddeliveryfujitsu.models.WeatherData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeatherDataRepository extends JpaRepository<WeatherData, Long> {

    @Query("SELECT w FROM WeatherData w WHERE w.stationName = :stationName ORDER BY w.timestamp DESC")
    List<WeatherData> findLatestByStationName(@Param("stationName") String stationName);

    @Query(value = "SELECT * FROM weather_data w WHERE w.station_name = :stationName " +
            "ORDER BY ABS(TIMESTAMPDIFF(SECOND, w.timestamp, :dateTime)) LIMIT 1", nativeQuery = true)
    Optional<WeatherData> findClosestByStationNameAndDateTime(
            @Param("stationName") String stationName,
            @Param("dateTime") LocalDateTime dateTime);
    List<WeatherData> findByWmoCode(String wmoCode);
}
