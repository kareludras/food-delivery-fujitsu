package com.example.fooddeliveryfujitsu.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class DeliveryFeeRequest {

    public enum City {
        TALLINN, TARTU, PARNU
    }

    public enum VehicleType {
        CAR, SCOOTER, BIKE
    }

    @NotNull(message = "City is required")
    private City city;

    @NotNull(message = "Vehicle type is required")
    private VehicleType vehicleType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateTime;

    public DeliveryFeeRequest() {
    }

    public DeliveryFeeRequest(City city, VehicleType vehicleType) {
        this.city = city;
        this.vehicleType = vehicleType;
    }

    public DeliveryFeeRequest(City city, VehicleType vehicleType, LocalDateTime dateTime) {
        this.city = city;
        this.vehicleType = vehicleType;
        this.dateTime = dateTime;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return "DeliveryFeeRequest{" +
                "city=" + city +
                ", vehicleType=" + vehicleType +
                ", dateTime=" + dateTime +
                '}';
    }
}