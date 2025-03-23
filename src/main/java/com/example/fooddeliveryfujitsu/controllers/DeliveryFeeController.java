package com.example.fooddeliveryfujitsu.controllers;

import com.example.fooddeliveryfujitsu.models.DeliveryFeeRequest;
import com.example.fooddeliveryfujitsu.models.DeliveryFeeResponse;
import com.example.fooddeliveryfujitsu.services.DeliveryFeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/delivery-fee")
@Tag(name = "Delivery Fee", description = "API for calculating delivery fees based on city, vehicle type, and weather conditions")
public class DeliveryFeeController {

    private static final Logger logger = LoggerFactory.getLogger(DeliveryFeeController.class);

    private final DeliveryFeeService deliveryFeeService;

    @Autowired
    public DeliveryFeeController(DeliveryFeeService deliveryFeeService) {
        this.deliveryFeeService = deliveryFeeService;
    }

    @PostMapping("/calculate")
    @Operation(summary = "Calculate delivery fee",
            description = "Calculates the delivery fee based on city, vehicle type, and current weather conditions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully calculated fee",
                    content = @Content(schema = @Schema(implementation = DeliveryFeeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    public ResponseEntity<?> calculateFee(
            @Parameter(description = "Delivery fee request details", required = true)
            @Valid @RequestBody DeliveryFeeRequest request) {

        logger.info("Received delivery fee calculation request: {}", request);

        try {
            DeliveryFeeResponse response = deliveryFeeService.calculateDeliveryFee(request);

            if (response.hasError()) {
                logger.warn("Delivery fee calculation error: {}", response.getError());
                return ResponseEntity.badRequest().body(response);
            }

            logger.info("Calculated delivery fee: {}", response.getFee());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error calculating delivery fee", e);
            return ResponseEntity.badRequest().body(new DeliveryFeeResponse("Error calculating delivery fee: " + e.getMessage()));
        }
    }

    @GetMapping("/test")
    public String test() {
        return "API is working!";
    }
}