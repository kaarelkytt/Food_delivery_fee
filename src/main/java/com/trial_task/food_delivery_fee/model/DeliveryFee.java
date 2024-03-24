package com.trial_task.food_delivery_fee.model;

/**
 * Represents a delivery fee, which is calculated based on various factors.
 */
public class DeliveryFee {
    private final String city;
    private final String vehicleType;
    private final double regionalBaseFee;
    private final double airTemperatureExtraFee;
    private final double windSpeedExtraFee;
    private final double weatherPhenomenonExtraFee;
    private final double totalFee;

    /**
     * Constructs a DeliveryFee object with the given parameters.
     *
     * @param city The city where the delivery is to be made.
     * @param vehicleType The type of vehicle used for the delivery.
     * @param regionalBaseFee The regional base fee for the delivery.
     * @param airTemperatureExtraFee The extra fee based on air temperature.
     * @param windSpeedExtraFee The extra fee based on wind speed.
     * @param weatherPhenomenonExtraFee The extra fee based on weather phenomenon.
     */
    public DeliveryFee(String city, String vehicleType, double regionalBaseFee, double airTemperatureExtraFee, double windSpeedExtraFee, double weatherPhenomenonExtraFee) {
        this.city = city;
        this.vehicleType = vehicleType;
        this.regionalBaseFee = regionalBaseFee;
        this.airTemperatureExtraFee = airTemperatureExtraFee;
        this.windSpeedExtraFee = windSpeedExtraFee;
        this.weatherPhenomenonExtraFee = weatherPhenomenonExtraFee;
        this.totalFee = totalFee();
    }

    public double getRegionalBaseFee() {
        return regionalBaseFee;
    }

    public double getAirTemperatureExtraFee() {
        return airTemperatureExtraFee;
    }

    public double getWindSpeedExtraFee() {
        return windSpeedExtraFee;
    }

    public double getWeatherPhenomenonExtraFee() {
        return weatherPhenomenonExtraFee;
    }

    public double getTotalFee() {
        return totalFee;
    }

    /**
     * Calculates the total delivery fee.
     *
     * @return The total delivery fee.
     */
    private double totalFee(){
        return regionalBaseFee + airTemperatureExtraFee + windSpeedExtraFee + weatherPhenomenonExtraFee;
    }

    @Override
    public String toString() {
        return "DeliveryFee{" +
                "city='" + city + '\'' +
                ", vehicleType='" + vehicleType + '\'' +
                ", regionalBaseFee=" + regionalBaseFee +
                ", airTemperatureExtraFee=" + airTemperatureExtraFee +
                ", windSpeedExtraFee=" + windSpeedExtraFee +
                ", weatherPhenomenonExtraFee=" + weatherPhenomenonExtraFee +
                ", totalFee=" + totalFee +
                '}';
    }
}
