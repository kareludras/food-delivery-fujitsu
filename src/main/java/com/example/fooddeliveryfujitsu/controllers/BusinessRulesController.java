package com.example.fooddeliveryfujitsu.controllers;

import com.example.fooddeliveryfujitsu.models.RegionalBaseFee;
import com.example.fooddeliveryfujitsu.models.WeatherExtraFee;
import com.example.fooddeliveryfujitsu.services.BusinessRulesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/business-rules")
@Tag(name = "Business Rules Management", description = "API for managing delivery fee business rules")
public class BusinessRulesController {

    private static final Logger logger = LoggerFactory.getLogger(BusinessRulesController.class);

    private final BusinessRulesService businessRulesService;

    @Autowired
    public BusinessRulesController(BusinessRulesService businessRulesService) {
        this.businessRulesService = businessRulesService;
    }

    @GetMapping("/regional-base-fees")
    @Operation(summary = "Get all regional base fees",
            description = "Retrieves all regional base fees")
    public ResponseEntity<List<RegionalBaseFee>> getAllRegionalBaseFees() {
        return ResponseEntity.ok(businessRulesService.getAllRegionalBaseFees());
    }

    @GetMapping("/regional-base-fees/{id}")
    @Operation(summary = "Get regional base fee by ID",
            description = "Retrieves a regional base fee by its ID")
    public ResponseEntity<RegionalBaseFee> getRegionalBaseFeeById(@PathVariable Long id) {
        return businessRulesService.getRegionalBaseFeeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/regional-base-fees")
    @Operation(summary = "Create a new regional base fee",
            description = "Creates a new regional base fee rule")
    public ResponseEntity<RegionalBaseFee> createRegionalBaseFee(@Valid @RequestBody RegionalBaseFee fee) {
        RegionalBaseFee savedFee = businessRulesService.saveRegionalBaseFee(fee);
        return ResponseEntity.ok(savedFee);
    }

    @PutMapping("/regional-base-fees/{id}")
    @Operation(summary = "Update a regional base fee",
            description = "Updates an existing regional base fee rule")
    public ResponseEntity<RegionalBaseFee> updateRegionalBaseFee(
            @PathVariable Long id,
            @Valid @RequestBody RegionalBaseFee fee) {

        if (!businessRulesService.existsRegionalBaseFeeById(id)) {
            return ResponseEntity.notFound().build();
        }

        fee.setId(id);
        RegionalBaseFee updatedFee = businessRulesService.saveRegionalBaseFee(fee);
        return ResponseEntity.ok(updatedFee);
    }

    @DeleteMapping("/regional-base-fees/{id}")
    @Operation(summary = "Delete a regional base fee",
            description = "Deletes an existing regional base fee rule")
    public ResponseEntity<Void> deleteRegionalBaseFee(@PathVariable Long id) {
        if (!businessRulesService.existsRegionalBaseFeeById(id)) {
            return ResponseEntity.notFound().build();
        }

        businessRulesService.deleteRegionalBaseFee(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/weather-extra-fees")
    @Operation(summary = "Get all weather extra fees",
            description = "Retrieves all weather extra fees")
    public ResponseEntity<List<WeatherExtraFee>> getAllWeatherExtraFees() {
        return ResponseEntity.ok(businessRulesService.getAllWeatherExtraFees());
    }

    @GetMapping("/weather-extra-fees/{id}")
    @Operation(summary = "Get weather extra fee by ID",
            description = "Retrieves a weather extra fee by its ID")
    public ResponseEntity<WeatherExtraFee> getWeatherExtraFeeById(@PathVariable Long id) {
        return businessRulesService.getWeatherExtraFeeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/weather-extra-fees")
    @Operation(summary = "Create a new weather extra fee",
            description = "Creates a new weather extra fee rule")
    public ResponseEntity<WeatherExtraFee> createWeatherExtraFee(@Valid @RequestBody WeatherExtraFee fee) {
        WeatherExtraFee savedFee = businessRulesService.saveWeatherExtraFee(fee);
        return ResponseEntity.ok(savedFee);
    }

    @PutMapping("/weather-extra-fees/{id}")
    @Operation(summary = "Update a weather extra fee",
            description = "Updates an existing weather extra fee rule")
    public ResponseEntity<WeatherExtraFee> updateWeatherExtraFee(
            @PathVariable Long id,
            @Valid @RequestBody WeatherExtraFee fee) {

        if (!businessRulesService.existsWeatherExtraFeeById(id)) {
            return ResponseEntity.notFound().build();
        }

        fee.setId(id);
        WeatherExtraFee updatedFee = businessRulesService.saveWeatherExtraFee(fee);
        return ResponseEntity.ok(updatedFee);
    }

    @DeleteMapping("/weather-extra-fees/{id}")
    @Operation(summary = "Delete a weather extra fee",
            description = "Deletes an existing weather extra fee rule")
    public ResponseEntity<Void> deleteWeatherExtraFee(@PathVariable Long id) {
        if (!businessRulesService.existsWeatherExtraFeeById(id)) {
            return ResponseEntity.notFound().build();
        }

        businessRulesService.deleteWeatherExtraFee(id);
        return ResponseEntity.noContent().build();
    }
}