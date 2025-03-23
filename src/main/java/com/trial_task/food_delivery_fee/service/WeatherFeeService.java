package com.trial_task.food_delivery_fee.service;

import com.trial_task.food_delivery_fee.exception.ForbiddenVehicleTypeException;
import com.trial_task.food_delivery_fee.model.WeatherData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for calculating additional delivery fees based on weather conditions.
 */
@Service
public class WeatherFeeService {

    @Value("#{'${fee.temperature.applicableVehicles}'.split(',')}")
    private List<String> temperatureApplicableVehicles;

    @Value("#{'${fee.temperature.thresholds}'.split(',')}")
    private List<Double> temperatureThresholds;

    @Value("#{'${fee.temperature.fees}'.split(',')}")
    private List<Double> temperatureFees;


    @Value("#{'${fee.windSpeed.applicableVehicles}'.split(',')}")
    private List<String> windSpeedApplicableVehicles;

    @Value("${fee.windSpeed.maxAllowedWindSpeed}")
    private double maxAllowedWindSpeed;

    @Value("#{'${fee.windSpeed.thresholds}'.split(',')}")
    private List<Double> windSpeedThresholds;

    @Value("#{'${fee.windSpeed.fees}'.split(',')}")
    private List<Double> windSpeedFees;


    @Value("#{'${fee.weatherPhenomenon.applicableVehicles}'.split(',')}")
    private List<String> weatherPhenomenonApplicableVehicles;

    @Value("#{'${fee.weatherPhenomenon.forbiddenTypes}'.split(',')}")
    private List<String> forbiddenWeatherPhenomena;

    @Value("#{'${fee.weatherPhenomenon.types}'.split(',')}")
    private List<String> weatherPhenomenaTypes;

    @Value("#{'${fee.weatherPhenomenon.fees}'.split(',')}")
    private List<Double> weatherPhenomenaFees;


    /**
     * Calculates the extra fee based on air temperature for a specific vehicle type.
     *
     * @param vehicleType The type of vehicle used for the delivery.
     * @param weatherData The weather data.
     * @return The extra fee based on air temperature.
     */
    public double calculateAirTemperatureExtraFee(String vehicleType, WeatherData weatherData) {
        // Check if the vehicle type is applicable for temperature-based fees
        if (!temperatureApplicableVehicles.contains(vehicleType)) {
            return 0;
        }

        double airTemp = weatherData.getAirTemperature();

        // Iterate through the temperature thresholds to determine the applicable fee
        for (int i = 0; i < temperatureThresholds.size(); i++) {
            if (airTemp < temperatureThresholds.get(i)) {
                return temperatureFees.get(i);
            }
        }

        return 0;
    }

    /**
     * Calculates the extra fee based on wind speed for a specific vehicle type.
     *
     * @param vehicleType The type of vehicle used for the delivery.
     * @param weatherData The weather data.
     * @return The extra fee based on wind speed.
     * @throws ForbiddenVehicleTypeException If the vehicle type is not allowed due to high wind speed.
     */
    public double calculateWindSpeedExtraFee(String vehicleType, WeatherData weatherData) throws ForbiddenVehicleTypeException {
        // Check if the vehicle type is applicable for wind speed-based fees
        if (!windSpeedApplicableVehicles.contains(vehicleType)) {
            return 0;
        }

        double windSpeed = weatherData.getWindSpeed();

        // Check if the wind speed exceeds the maximum allowed limit
        if (windSpeed > maxAllowedWindSpeed) {
            throw new ForbiddenVehicleTypeException("Usage of selected vehicle type is forbidden");
        }

        // Iterate through the wind speed thresholds to determine the applicable fee
        for (int i = 0; i < windSpeedThresholds.size(); i++) {
            if (windSpeed > windSpeedThresholds.get(i)) {
                return windSpeedFees.get(i);
            }
        }
        return 0;
    }

    /**
     * Calculates the extra fee based on weather phenomenon for a specific vehicle type.
     *
     * @param vehicleType The type of vehicle used for the delivery.
     * @param weatherData The weather data.
     * @return The extra fee based on weather phenomenon.
     * @throws ForbiddenVehicleTypeException If the vehicle type is not allowed due to certain weather conditions.
     */
    public double calculateWeatherPhenomenonExtraFee(String vehicleType, WeatherData weatherData) throws ForbiddenVehicleTypeException {
        // Check if the vehicle type is applicable for weather phenomenon-based fees
        if (!weatherPhenomenonApplicableVehicles.contains(vehicleType)) {
            return 0;
        }

        String weatherPhenomenon = weatherData.getWeatherPhenomenon().toLowerCase();

        // Check if the weather phenomenon is forbidden for the vehicle type
        for (String forbidden : forbiddenWeatherPhenomena) {
            if (weatherPhenomenon.contains(forbidden)) {
                throw new ForbiddenVehicleTypeException("Usage of selected vehicle type is forbidden");
            }
        }

        // Iterate through the weather phenomena types to determine the applicable fee
        for (int i = 0; i < weatherPhenomenaTypes.size(); i++) {
            if (weatherPhenomenon.contains(weatherPhenomenaTypes.get(i))) {
                return weatherPhenomenaFees.get(i);
            }
        }

        return 0;
    }
}

