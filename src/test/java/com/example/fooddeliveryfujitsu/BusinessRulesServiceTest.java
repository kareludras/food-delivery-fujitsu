package com.example.fooddeliveryfujitsu;

import com.example.fooddeliveryfujitsu.models.RegionalBaseFee;
import com.example.fooddeliveryfujitsu.models.WeatherExtraFee;
import com.example.fooddeliveryfujitsu.repositories.RegionalBaseFeeRepository;
import com.example.fooddeliveryfujitsu.repositories.WeatherExtraFeeRepository;
import com.example.fooddeliveryfujitsu.services.BusinessRulesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BusinessRulesServiceTest {

    @Mock
    private RegionalBaseFeeRepository regionalBaseFeeRepository;

    @Mock
    private WeatherExtraFeeRepository weatherExtraFeeRepository;

    @InjectMocks
    private BusinessRulesService businessRulesService;

    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        now = LocalDateTime.now();
    }

    @Test
    void testGetRegionalBaseFeeFromDatabase() {
        RegionalBaseFee fee = new RegionalBaseFee("TALLINN", "CAR", new BigDecimal("4.5"), now.minusDays(1), null);
        when(regionalBaseFeeRepository.findActiveRuleForCityAndVehicle("TALLINN", "CAR", now))
                .thenReturn(Optional.of(fee));

        BigDecimal result = businessRulesService.getRegionalBaseFee("TALLINN", "CAR", now);

        assertEquals(new BigDecimal("4.5"), result);
        verify(regionalBaseFeeRepository).findActiveRuleForCityAndVehicle("TALLINN", "CAR", now);
    }

    @Test
    void testGetRegionalBaseFeeFromDefaultValue() {
        when(regionalBaseFeeRepository.findActiveRuleForCityAndVehicle("TALLINN", "CAR", now))
                .thenReturn(Optional.empty());

        BigDecimal result = businessRulesService.getRegionalBaseFee("TALLINN", "CAR", now);

        assertEquals(new BigDecimal("4.0"), result);
        verify(regionalBaseFeeRepository).findActiveRuleForCityAndVehicle("TALLINN", "CAR", now);
    }

    @Test
    void testGetAirTemperatureExtraFeeWithLowTemperature() {
        BigDecimal result = businessRulesService.getAirTemperatureExtraFee("BIKE", -15.0, now);
        assertEquals(new BigDecimal("1.0"), result);

        result = businessRulesService.getAirTemperatureExtraFee("BIKE", -5.0, now);
        assertEquals(new BigDecimal("0.5"), result);

        result = businessRulesService.getAirTemperatureExtraFee("BIKE", 5.0, now);
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void testIsVehicleUsageForbidden() {
        assertTrue(businessRulesService.isVehicleUsageForbidden("BIKE", 25.0, "Clear"));

        assertFalse(businessRulesService.isVehicleUsageForbidden("BIKE", 15.0, "Clear"));

        assertTrue(businessRulesService.isVehicleUsageForbidden("BIKE", 5.0, "Thunder"));

        assertTrue(businessRulesService.isVehicleUsageForbidden("SCOOTER", 5.0, "Hail"));

        assertFalse(businessRulesService.isVehicleUsageForbidden("CAR", 25.0, "Thunder"));
    }

    @Test
    void testSaveAndGetRegionalBaseFee() {
        RegionalBaseFee fee = new RegionalBaseFee("TALLINN", "CAR", new BigDecimal("4.5"), now, null);
        when(regionalBaseFeeRepository.save(fee)).thenReturn(fee);
        when(regionalBaseFeeRepository.findById(1L)).thenReturn(Optional.of(fee));

        RegionalBaseFee savedFee = businessRulesService.saveRegionalBaseFee(fee);
        assertEquals(fee, savedFee);

        when(regionalBaseFeeRepository.findById(1L)).thenReturn(Optional.of(fee));
        Optional<RegionalBaseFee> retrievedFee = businessRulesService.getRegionalBaseFeeById(1L);
        assertTrue(retrievedFee.isPresent());
        assertEquals(fee, retrievedFee.get());
    }

    @Test
    void testDeleteRegionalBaseFee() {
        when(regionalBaseFeeRepository.existsById(1L)).thenReturn(true);

        businessRulesService.deleteRegionalBaseFee(1L);

        verify(regionalBaseFeeRepository).deleteById(1L);
    }
}
