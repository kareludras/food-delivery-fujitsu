package com.example.fooddeliveryfujitsu.models;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Table(name = "regional_base_fee")
public class RegionalBaseFee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private String city;

    @NotNull
    @Column(nullable = false)
    private String vehicleType;

    @NotNull
    @Positive
    @Column(nullable = false)
    private BigDecimal fee;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime validFrom;

    @Column
    private LocalDateTime validTo;

    // Constructors
    public RegionalBaseFee() {
    }

    public RegionalBaseFee(String city, String vehicleType, BigDecimal fee, LocalDateTime validFrom, LocalDateTime validTo) {
        this.city = city;
        this.vehicleType = vehicleType;
        this.fee = fee;
        this.validFrom = validFrom;
        this.validTo = validTo;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
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
        return "RegionalBaseFee{" +
                "id=" + id +
                ", city='" + city + '\'' +
                ", vehicleType='" + vehicleType + '\'' +
                ", fee=" + fee +
                ", validFrom=" + validFrom +
                ", validTo=" + validTo +
                '}';
    }
}
