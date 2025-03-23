package com.example.fooddeliveryfujitsu.services;

import com.example.fooddeliveryfujitsu.models.RegionalBaseFee;
import com.example.fooddeliveryfujitsu.models.WeatherExtraFee;
import com.example.fooddeliveryfujitsu.repositories.RegionalBaseFeeRepository;
import com.example.fooddeliveryfujitsu.repositories.WeatherExtraFeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class BusinessRulesService {

    private static final Logger logger = LoggerFactory.getLogger(BusinessRulesService.class);

    private final RegionalBaseFeeRepository regionalBaseFeeRepository;
    private final WeatherExtraFeeRepository weatherExtraFeeRepository;

    private final Map<String, Map<String, BigDecimal>> regionalBaseFeeCache = new HashMap<>();

    @Autowired
    public BusinessRulesService(RegionalBaseFeeRepository regionalBaseFeeRepository,
                                WeatherExtraFeeRepository weatherExtraFeeRepository) {
        this.regionalBaseFeeRepository = regionalBaseFeeRepository;
        this.weatherExtraFeeRepository = weatherExtraFeeRepository;
    }

    public List<RegionalBaseFee> getAllRegionalBaseFees() {
        return regionalBaseFeeRepository.findAll();
    }

    public Optional<RegionalBaseFee> getRegionalBaseFeeById(Long id) {
        return regionalBaseFeeRepository.findById(id);
    }

    public RegionalBaseFee saveRegionalBaseFee(RegionalBaseFee fee) {
        RegionalBaseFee savedFee = regionalBaseFeeRepository.save(fee);
        // Invalidate cache
        regionalBaseFeeCache.clear();
        return savedFee;
    }

    public boolean existsRegionalBaseFeeById(Long id) {
        return regionalBaseFeeRepository.existsById(id);
    }

    public void deleteRegionalBaseFee(Long id) {
        regionalBaseFeeRepository.deleteById(id);
        // Invalidate cache
        regionalBaseFeeCache.clear();
    }

    public BigDecimal getRegionalBaseFee(String city, String vehicleType, LocalDateTime dateTime) {
        // First, try to get from the database
        Optional<RegionalBaseFee> feeRule = regionalBaseFeeRepository
                .findActiveRuleForCityAndVehicle(city, vehicleType, dateTime);

        if (feeRule.isPresent()) {
            return feeRule.get().getFee();
        }

        String cacheKey = city + "_" + vehicleType;
        if (!regionalBaseFeeCache.containsKey(city)) {
            initializeDefaultFeesForCity(city);
        }

        Map<String, BigDecimal> cityFees = regionalBaseFeeCache.get(city);
        if (cityFees != null && cityFees.containsKey(vehicleType)) {
            return cityFees.get(vehicleType);
        }

        return BigDecimal.ZERO;
    }

    private void initializeDefaultFeesForCity(String city) {
        Map<String, BigDecimal> cityFees = new HashMap<>();

        switch (city) {
            case "TALLINN":
                cityFees.put("CAR", new BigDecimal("4.0"));
                cityFees.put("SCOOTER", new BigDecimal("3.5"));
                cityFees.put("BIKE", new BigDecimal("3.0"));
                break;
            case "TARTU":
                cityFees.put("CAR", new BigDecimal("3.5"));
                cityFees.put("SCOOTER", new BigDecimal("3.0"));
                cityFees.put("BIKE", new BigDecimal("2.5"));
                break;
            case "PARNU":
                cityFees.put("CAR", new BigDecimal("3.0"));
                cityFees.put("SCOOTER", new BigDecimal("2.5"));
                cityFees.put("BIKE", new BigDecimal("2.0"));
                break;
            default:
                break;
        }

        regionalBaseFeeCache.put(city, cityFees);
    }

    // Weather Extra Fee methods
    public List<WeatherExtraFee> getAllWeatherExtraFees() {
        return weatherExtraFeeRepository.findAll();
    }

    public Optional<WeatherExtraFee> getWeatherExtraFeeById(Long id) {
        return weatherExtraFeeRepository.findById(id);
    }

    public WeatherExtraFee saveWeatherExtraFee(WeatherExtraFee fee) {
        return weatherExtraFeeRepository.save(fee);
    }

    public boolean existsWeatherExtraFeeById(Long id) {
        return weatherExtraFeeRepository.existsById(id);
    }

    public void deleteWeatherExtraFee(Long id) {
        weatherExtraFeeRepository.deleteById(id);
    }

    public BigDecimal getAirTemperatureExtraFee(String vehicleType, Double temperature, LocalDateTime dateTime) {
        if (vehicleType.equals("CAR") || temperature == null) {
            return BigDecimal.ZERO;
        }

        Optional<WeatherExtraFee> feeRule = weatherExtraFeeRepository
                .findActiveAirTempRuleForVehicle(vehicleType, temperature, dateTime);

        if (feeRule.isPresent()) {
            return feeRule.get().getFee();
        }

        if (temperature < -10.0) {
            return new BigDecimal("1.0");
        } else if (temperature < 0.0) {
            return new BigDecimal("0.5");
        }

        return BigDecimal.ZERO;
    }

    public BigDecimal getWindSpeedExtraFee(String vehicleType, Double windSpeed, LocalDateTime dateTime) {
        if (!vehicleType.equals("BIKE") || windSpeed == null) {
            return BigDecimal.ZERO;
        }

        Optional<WeatherExtraFee> feeRule = weatherExtraFeeRepository
                .findActiveWindSpeedRuleForVehicle(vehicleType, windSpeed, dateTime);

        if (feeRule.isPresent()) {
            return feeRule.get().getFee();
        }

        if (windSpeed >= 10.0 && windSpeed <= 20.0) {
            return new BigDecimal("0.5");
        }

        return BigDecimal.ZERO;
    }

    public BigDecimal getWeatherPhenomenonExtraFee(String vehicleType, String phenomenon, LocalDateTime dateTime) {
        if (vehicleType.equals("CAR") || phenomenon == null || phenomenon.isEmpty()) {
            return BigDecimal.ZERO;
        }

        String phenomenonCategory = categorizeWeatherPhenomenon(phenomenon);

        if (phenomenonCategory == null) {
            return BigDecimal.ZERO;
        }

        Optional<WeatherExtraFee> feeRule = weatherExtraFeeRepository
                .findActivePhenomenonRuleForVehicle(vehicleType, phenomenonCategory, dateTime);

        if (feeRule.isPresent()) {
            return feeRule.get().getFee();
        }

        if ("SNOW_SLEET".equals(phenomenonCategory)) {
            return new BigDecimal("1.0");
        } else if ("RAIN".equals(phenomenonCategory)) {
            return new BigDecimal("0.5");
        }

        return BigDecimal.ZERO;
    }

    public boolean isVehicleUsageForbidden(String vehicleType, Double windSpeed, String phenomenon) {
        if (vehicleType.equals("BIKE") && windSpeed != null && windSpeed > 20.0) {
            return true;
        }

        if ((vehicleType.equals("BIKE") || vehicleType.equals("SCOOTER")) &&
                phenomenon != null && !phenomenon.isEmpty()) {
            String phenomenonCategory = categorizeWeatherPhenomenon(phenomenon);
            return "FORBIDDEN".equals(phenomenonCategory);
        }

        return false;
    }

    private String categorizeWeatherPhenomenon(String phenomenon) {
        if (phenomenon == null || phenomenon.isEmpty()) {
            return null;
        }

        phenomenon = phenomenon.toLowerCase();

        // Snow or sleet related phenomena
        if (phenomenon.contains("snow") || phenomenon.contains("sleet")) {
            return "SNOW_SLEET";
        }

        // Rain related phenomena
        if (phenomenon.contains("rain") || phenomenon.contains("shower") || phenomenon.contains("drizzle")) {
            return "RAIN";
        }

        // Forbidden phenomena
        if (phenomenon.contains("glaze") || phenomenon.contains("hail") || phenomenon.contains("thunder")) {
            return "FORBIDDEN";
        }

        return null;
    }
}
