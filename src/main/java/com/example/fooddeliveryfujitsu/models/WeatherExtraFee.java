package com.example.fooddeliveryfujitsu.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "weather_extra_fee")
public class WeatherExtraFee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private String feeType;

    @NotNull
    @Column(nullable = false)
    private String vehicleType;

    @Column
    private Double minValue;

    @Column
    private Double maxValue;

    @Column
    private String phenomenonCategory;

    @NotNull
    @Positive
    @Column(nullable = false)
    private BigDecimal fee;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime validFrom;

    @Column
    private LocalDateTime validTo;

    public WeatherExtraFee() {
    }

    public WeatherExtraFee(String feeType, String vehicleType, Double minValue, Double maxValue,
                           String phenomenonCategory, BigDecimal fee,
                           LocalDateTime validFrom, LocalDateTime validTo) {
        this.feeType = feeType;
        this.vehicleType = vehicleType;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.phenomenonCategory = phenomenonCategory;
        this.fee = fee;
        this.validFrom = validFrom;
        this.validTo = validTo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFeeType() {
        return feeType;
    }

    public void setFeeType(String feeType) {
        this.feeType = feeType;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public Double getMinValue() {
        return minValue;
    }

    public void setMinValue(Double minValue) {
        this.minValue = minValue;
    }

    public Double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Double maxValue) {
        this.maxValue = maxValue;
    }

    public String getPhenomenonCategory() {
        return phenomenonCategory;
    }

    public void setPhenomenonCategory(String phenomenonCategory) {
        this.phenomenonCategory = phenomenonCategory;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDateTime getValidTo() {
        return validTo;
    }

    public void setValidTo(LocalDateTime validTo) {
        this.validTo = validTo;
    }

    @Override
    public String toString() {
        return "WeatherExtraFee{" +
                "id=" + id +
                ", feeType='" + feeType + '\'' +
                ", vehicleType='" + vehicleType + '\'' +
                ", minValue=" + minValue +
                ", maxValue=" + maxValue +
                ", phenomenonCategory='" + phenomenonCategory + '\'' +
                ", fee=" + fee +
                ", validFrom=" + validFrom +
                ", validTo=" + validTo +
                '}';
    }
}