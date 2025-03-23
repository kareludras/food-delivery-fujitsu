package com.example.fooddeliveryfujitsu;

import com.example.fooddeliveryfujitsu.models.DeliveryFeeRequest;
import com.example.fooddeliveryfujitsu.models.WeatherData;
import com.example.fooddeliveryfujitsu.repositories.WeatherDataRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DeliveryFeeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WeatherDataRepository weatherDataRepository;

    @BeforeEach
    void setUp() {
        weatherDataRepository.deleteAll();
        LocalDateTime now = LocalDateTime.now();

        weatherDataRepository.save(new WeatherData(
                "Tallinn-Harku",
                "26038",
                -5.0,
                8.0,
                "Light snow",
                now
        ));


        weatherDataRepository.save(new WeatherData(
                "Tartu-Tõravere",
                "26242",
                -2.0,
                5.0,
                "Light snow shower",
                now
        ));

        // Pärnu weather data
        weatherDataRepository.save(new WeatherData(
                "Pärnu",
                "41803",
                0.0,
                12.0,
                "Rain",
                now
        ));
    }

    @Test
    void testCalculateDeliveryFeeForTallinnBike() throws Exception {
        DeliveryFeeRequest request = new DeliveryFeeRequest(
                DeliveryFeeRequest.City.TALLINN,
                DeliveryFeeRequest.VehicleType.BIKE
        );

        mockMvc.perform(post("/api/delivery-fee/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fee").isNumber())
                .andExpect(jsonPath("$.breakdown.regionalBaseFee").value(3.0))
                .andExpect(jsonPath("$.breakdown.airTemperatureExtraFee").value(0.5))
                .andExpect(jsonPath("$.breakdown.weatherPhenomenonExtraFee").value(0.0));
    }

    @Test
    void testCalculateDeliveryFeeForParnuBike() throws Exception {
        DeliveryFeeRequest request = new DeliveryFeeRequest(
                DeliveryFeeRequest.City.PARNU,
                DeliveryFeeRequest.VehicleType.BIKE
        );

        mockMvc.perform(post("/api/delivery-fee/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fee").isNumber())
                .andExpect(jsonPath("$.breakdown.windSpeedExtraFee").value(0.5))
                .andExpect(jsonPath("$.breakdown.weatherPhenomenonExtraFee").value(0.0));
    }
    @Test
    void testCalculateDeliveryFeeForCarNoExtraFees() throws Exception {
        DeliveryFeeRequest request = new DeliveryFeeRequest(
                DeliveryFeeRequest.City.TALLINN,
                DeliveryFeeRequest.VehicleType.CAR
        );

        mockMvc.perform(post("/api/delivery-fee/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fee").value(4.0))
                .andExpect(jsonPath("$.breakdown.airTemperatureExtraFee").value(0))
                .andExpect(jsonPath("$.breakdown.windSpeedExtraFee").value(0))
                .andExpect(jsonPath("$.breakdown.weatherPhenomenonExtraFee").value(0));
    }
}
