package com.example.fooddeliveryfujitsu;

import com.example.fooddeliveryfujitsu.models.DeliveryFeeRequest;
import com.example.fooddeliveryfujitsu.models.DeliveryFeeResponse;
import com.example.fooddeliveryfujitsu.models.WeatherData;
import com.example.fooddeliveryfujitsu.services.BusinessRulesService;
import com.example.fooddeliveryfujitsu.services.DeliveryFeeService;
import com.example.fooddeliveryfujitsu.services.WeatherDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class DeliveryFeeServiceTest {

    @Mock
    private WeatherDataService weatherDataService;

    @Mock
    private BusinessRulesService businessRulesService;

    @InjectMocks
    private DeliveryFeeService deliveryFeeService;

    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        now = LocalDateTime.now();
    }

    @Test
    void testCalculateDeliveryFeeForTartuBikeExample() {
        DeliveryFeeRequest request = new DeliveryFeeRequest(
                DeliveryFeeRequest.City.TARTU,
                DeliveryFeeRequest.VehicleType.BIKE
        );

        WeatherData weatherData = new WeatherData(
                "Tartu-Tõravere",
                "26242",
                -2.1,
                4.7,
                "Light snow shower",
                now
        );

        when(weatherDataService.getLatestWeatherDataForCity(eq("TARTU"))).thenReturn(Optional.of(weatherData));
        when(businessRulesService.getRegionalBaseFee(eq("TARTU"), eq("BIKE"), any())).thenReturn(new BigDecimal("2.5"));
        when(businessRulesService.getAirTemperatureExtraFee(eq("BIKE"), eq(-2.1), any())).thenReturn(new BigDecimal("0.5"));
        when(businessRulesService.getWindSpeedExtraFee(eq("BIKE"), eq(4.7), any())).thenReturn(BigDecimal.ZERO);
        when(businessRulesService.getWeatherPhenomenonExtraFee(eq("BIKE"), eq("Light snow shower"), any())).thenReturn(new BigDecimal("1.0"));
        when(businessRulesService.isVehicleUsageForbidden(eq("BIKE"), eq(4.7), eq("Light snow shower"))).thenReturn(false);

        DeliveryFeeResponse response = deliveryFeeService.calculateDeliveryFee(request);

        assertFalse(response.hasError());
        assertEquals(new BigDecimal("4.0"), response.getFee());
        assertEquals(new BigDecimal("2.5"), response.getBreakdown().getRegionalBaseFee());
        assertEquals(new BigDecimal("0.5"), response.getBreakdown().getAirTemperatureExtraFee());
        assertEquals(BigDecimal.ZERO, response.getBreakdown().getWindSpeedExtraFee());
        assertEquals(new BigDecimal("1.0"), response.getBreakdown().getWeatherPhenomenonExtraFee());
    }

    @Test
    void testCalculateDeliveryFeeWithForbiddenWindSpeed() {
        DeliveryFeeRequest request = new DeliveryFeeRequest(
                DeliveryFeeRequest.City.PARNU,
                DeliveryFeeRequest.VehicleType.BIKE
        );

        WeatherData weatherData = new WeatherData(
                "Pärnu",
                "41803",
                5.0,
                22.0, // Wind speed > 20 m/s
                "Clear",
                now
        );

        when(weatherDataService.getLatestWeatherDataForCity(eq("PARNU"))).thenReturn(Optional.of(weatherData));
        when(businessRulesService.isVehicleUsageForbidden(eq("BIKE"), eq(22.0), eq("Clear"))).thenReturn(true);

        DeliveryFeeResponse response = deliveryFeeService.calculateDeliveryFee(request);

        assertTrue(response.hasError());
        assertEquals("Usage of selected vehicle type is forbidden", response.getError());
    }

    @Test
    void testCalculateDeliveryFeeWithForbiddenWeatherPhenomenon() {
        // Setup request
        DeliveryFeeRequest request = new DeliveryFeeRequest(
                DeliveryFeeRequest.City.TALLINN,
                DeliveryFeeRequest.VehicleType.SCOOTER
        );

        WeatherData weatherData = new WeatherData(
                "Tallinn-Harku",
                "26038",
                10.0,
                5.0,
                "Thunder", // Forbidden phenomenon
                now
        );

        when(weatherDataService.getLatestWeatherDataForCity(eq("TALLINN"))).thenReturn(Optional.of(weatherData));
        when(businessRulesService.isVehicleUsageForbidden(eq("SCOOTER"), eq(5.0), eq("Thunder"))).thenReturn(true);

        DeliveryFeeResponse response = deliveryFeeService.calculateDeliveryFee(request);

        assertTrue(response.hasError());
        assertEquals("Usage of selected vehicle type is forbidden", response.getError());
    }

    @Test
    void testCalculateDeliveryFeeWithHistoricalData() {
        LocalDateTime historicalDate = LocalDateTime.of(2023, 1, 1, 12, 0);
        DeliveryFeeRequest request = new DeliveryFeeRequest(
                DeliveryFeeRequest.City.TALLINN,
                DeliveryFeeRequest.VehicleType.CAR,
                historicalDate
        );

        WeatherData weatherData = new WeatherData(
                "Tallinn-Harku",
                "26038",
                -5.0,
                3.0,
                "Light snow",
                historicalDate
        );

        when(weatherDataService.getWeatherDataForCityAtTime(eq("TALLINN"), eq(historicalDate))).thenReturn(Optional.of(weatherData));
        when(businessRulesService.getRegionalBaseFee(eq("TALLINN"), eq("CAR"), eq(historicalDate))).thenReturn(new BigDecimal("4.0"));
        when(businessRulesService.isVehicleUsageForbidden(eq("CAR"), eq(3.0), eq("Light snow"))).thenReturn(false);

        DeliveryFeeResponse response = deliveryFeeService.calculateDeliveryFee(request);

        assertFalse(response.hasError());
        assertEquals(new BigDecimal("4.0"), response.getFee()); // Car has no extra fees
    }

    @Test
    void testCalculateDeliveryFeeWithMissingWeatherData() {
        DeliveryFeeRequest request = new DeliveryFeeRequest(
                DeliveryFeeRequest.City.PARNU,
                DeliveryFeeRequest.VehicleType.BIKE
        );

        when(weatherDataService.getLatestWeatherDataForCity(eq("PARNU"))).thenReturn(Optional.empty());

        DeliveryFeeResponse response = deliveryFeeService.calculateDeliveryFee(request);

        assertTrue(response.hasError());
        assertEquals("Weather data not available for the specified city", response.getError());
    }
}