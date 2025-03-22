package com.example.fooddeliveryfujitsu.models;

import java.math.BigDecimal;

public class DeliveryFeeResponse {

    private BigDecimal fee;
    private String error;
    private FeeBreakdown breakdown;

    public static class FeeBreakdown {
        private BigDecimal regionalBaseFee;
        private BigDecimal airTemperatureExtraFee;
        private BigDecimal windSpeedExtraFee;
        private BigDecimal weatherPhenomenonExtraFee;

        public FeeBreakdown(BigDecimal regionalBaseFee, BigDecimal airTemperatureExtraFee,
                            BigDecimal windSpeedExtraFee, BigDecimal weatherPhenomenonExtraFee) {
            this.regionalBaseFee = regionalBaseFee;
            this.airTemperatureExtraFee = airTemperatureExtraFee;
            this.windSpeedExtraFee = windSpeedExtraFee;
            this.weatherPhenomenonExtraFee = weatherPhenomenonExtraFee;
        }

        public BigDecimal getRegionalBaseFee() {
            return regionalBaseFee;
        }

        public void setRegionalBaseFee(BigDecimal regionalBaseFee) {
            this.regionalBaseFee = regionalBaseFee;
        }

        public BigDecimal getAirTemperatureExtraFee() {
            return airTemperatureExtraFee;
        }

        public void setAirTemperatureExtraFee(BigDecimal airTemperatureExtraFee) {
            this.airTemperatureExtraFee = airTemperatureExtraFee;
        }

        public BigDecimal getWindSpeedExtraFee() {
            return windSpeedExtraFee;
        }

        public void setWindSpeedExtraFee(BigDecimal windSpeedExtraFee) {
            this.windSpeedExtraFee = windSpeedExtraFee;
        }

        public BigDecimal getWeatherPhenomenonExtraFee() {
            return weatherPhenomenonExtraFee;
        }

        public void setWeatherPhenomenonExtraFee(BigDecimal weatherPhenomenonExtraFee) {
            this.weatherPhenomenonExtraFee = weatherPhenomenonExtraFee;
        }
    }

    public DeliveryFeeResponse(BigDecimal fee, FeeBreakdown breakdown) {
        this.fee = fee;
        this.breakdown = breakdown;
    }

    public DeliveryFeeResponse(String error) {
        this.error = error;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public FeeBreakdown getBreakdown() {
        return breakdown;
    }

    public void setBreakdown(FeeBreakdown breakdown) {
        this.breakdown = breakdown;
    }

    public boolean hasError() {
        return error != null && !error.isEmpty();
    }
}
