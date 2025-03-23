package com.example.fooddeliveryfujitsu.services;

import com.example.fooddeliveryfujitsu.models.DeliveryFeeRequest;
import com.example.fooddeliveryfujitsu.models.DeliveryFeeResponse;
import com.example.fooddeliveryfujitsu.models.WeatherData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DeliveryFeeService {
    private static final Logger logger = LoggerFactory.getLogger(DeliveryFeeService.class);
    private static final Map<String, Map<String, BigDecimal>> REGIONAL_BASE_FEES = new HashMap<>();
    private static final List<String> SNOW_SLEET_PHENOMENA = Arrays.asList(
            "Light snow shower", "Moderate snow shower", "Heavy snow shower",
            "Light sleet", "Moderate sleet", "Light snowfall", "Moderate snowfall",
            "Heavy snowfall", "Blowing snow", "Drifting snow"
    );
    private static final List<String> RAIN_PHENOMENA = Arrays.asList(
            "Light rain", "Moderate rain", "Heavy rain", "Light shower",
            "Moderate shower", "Heavy shower", "Light rain shower", "Moderate rain shower",
            "Heavy rain shower", "Drizzle"
    );
    private static final List<String> FORBIDDEN_PHENOMENA = Arrays.asList(
            "Glaze", "Hail", "Thunder"
    );

    static {
        // Initialize Tallinn fees
        Map<String, BigDecimal> tallinnFees = new HashMap<>();
        tallinnFees.put("CAR", new BigDecimal("4.0"));
        tallinnFees.put("SCOOTER", new BigDecimal("3.5"));
        tallinnFees.put("BIKE", new BigDecimal("3.0"));
        REGIONAL_BASE_FEES.put("TALLINN", tallinnFees);

        // Initialize Tartu fees
        Map<String, BigDecimal> tartuFees = new HashMap<>();
        tartuFees.put("CAR", new BigDecimal("3.5"));
        tartuFees.put("SCOOTER", new BigDecimal("3.0"));
        tartuFees.put("BIKE", new BigDecimal("2.5"));
        REGIONAL_BASE_FEES.put("TARTU", tartuFees);

        // Initialize Pärnu fees
        Map<String, BigDecimal> parnuFees = new HashMap<>();
        parnuFees.put("CAR", new BigDecimal("3.0"));
        parnuFees.put("SCOOTER", new BigDecimal("2.5"));
        parnuFees.put("BIKE", new BigDecimal("2.0"));
        REGIONAL_BASE_FEES.put("PARNU", parnuFees);
    }

    private final WeatherDataService weatherDataService;

    @Autowired
    public DeliveryFeeService(WeatherDataService weatherDataService) {
        this.weatherDataService = weatherDataService;
    }

    public DeliveryFeeResponse calculateDeliveryFee(DeliveryFeeRequest request) {
        logger.info("Calculating delivery fee for: {}", request);

        String city = request.getCity().name();
        String vehicleType = request.getVehicleType().name();
        BigDecimal regionalBaseFee = getRegionalBaseFee(city, vehicleType);
        Optional<WeatherData> weatherDataOpt;
        if (request.getDateTime() != null) {
            weatherDataOpt = weatherDataService.getWeatherDataForCityAtTime(city, request.getDateTime());
        } else {
            weatherDataOpt = weatherDataService.getLatestWeatherDataForCity(city);
        }

        if (weatherDataOpt.isEmpty()) {
            return new DeliveryFeeResponse("Weather data not available for the specified city");
        }

        WeatherData weatherData = weatherDataOpt.get();
        logger.info("Using weather data: {}", weatherData);

        String restrictionMessage = checkVehicleRestrictions(vehicleType, weatherData);
        if (restrictionMessage != null) {
            return new DeliveryFeeResponse(restrictionMessage);
        }

        BigDecimal airTemperatureExtraFee = calculateAirTemperatureExtraFee(vehicleType, weatherData.getAirTemperature());
        BigDecimal windSpeedExtraFee = calculateWindSpeedExtraFee(vehicleType, weatherData.getWindSpeed());
        BigDecimal weatherPhenomenonExtraFee = calculateWeatherPhenomenonExtraFee(vehicleType, weatherData.getWeatherPhenomenon());

        BigDecimal totalFee = regionalBaseFee
                .add(airTemperatureExtraFee)
                .add(windSpeedExtraFee)
                .add(weatherPhenomenonExtraFee);

        DeliveryFeeResponse.FeeBreakdown breakdown = new DeliveryFeeResponse.FeeBreakdown(
                regionalBaseFee,
                airTemperatureExtraFee,
                windSpeedExtraFee,
                weatherPhenomenonExtraFee
        );

        logger.info("Calculated delivery fee: {}", totalFee);
        return new DeliveryFeeResponse(totalFee, breakdown);
    }

    private BigDecimal getRegionalBaseFee(String city, String vehicleType) {
        Map<String, BigDecimal> cityFees = REGIONAL_BASE_FEES.get(city);
        if (cityFees != null) {
            return cityFees.getOrDefault(vehicleType, BigDecimal.ZERO);
        }
        return BigDecimal.ZERO;
    }

    private String checkVehicleRestrictions(String vehicleType, WeatherData weatherData) {
        if (vehicleType.equals("CAR")) {
            return null;
        }

        if (vehicleType.equals("BIKE") && weatherData.getWindSpeed() > 20.0) {
            return "Usage of selected vehicle type is forbidden";
        }

        String phenomenon = weatherData.getWeatherPhenomenon();
        if ((vehicleType.equals("BIKE") || vehicleType.equals("SCOOTER")) &&
                FORBIDDEN_PHENOMENA.stream().anyMatch(p -> phenomenon.toLowerCase().contains(p.toLowerCase()))) {
            return "Usage of selected vehicle type is forbidden";
        }

        return null;
    }

    private BigDecimal calculateAirTemperatureExtraFee(String vehicleType, Double temperature) {
        if (vehicleType.equals("CAR") || temperature == null) {
            return BigDecimal.ZERO;
        }

        if (temperature < -10.0) {
            return new BigDecimal("1.0");
        } else if (temperature < 0.0) {
            return new BigDecimal("0.5");
        }

        return BigDecimal.ZERO;
    }

    private BigDecimal calculateWindSpeedExtraFee(String vehicleType, Double windSpeed) {
        if (!vehicleType.equals("BIKE") || windSpeed == null) {
            return BigDecimal.ZERO;
        }

        if (windSpeed >= 10.0 && windSpeed <= 20.0) {
            return new BigDecimal("0.5");
        }

        return BigDecimal.ZERO;
    }

    private BigDecimal calculateWeatherPhenomenonExtraFee(String vehicleType, String phenomenon) {
        if (vehicleType.equals("CAR") || phenomenon == null || phenomenon.isEmpty()) {
            return BigDecimal.ZERO;
        }

        if (SNOW_SLEET_PHENOMENA.stream().anyMatch(p -> phenomenon.toLowerCase().contains(p.toLowerCase()))) {
            return new BigDecimal("1.0");
        }

        if (RAIN_PHENOMENA.stream().anyMatch(p -> phenomenon.toLowerCase().contains(p.toLowerCase()))) {
            return new BigDecimal("0.5");
        }

        return BigDecimal.ZERO;
    }
}
