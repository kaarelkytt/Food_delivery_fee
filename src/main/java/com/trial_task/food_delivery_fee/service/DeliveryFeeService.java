package com.trial_task.food_delivery_fee.service;

import com.trial_task.food_delivery_fee.exception.ForbiddenCityException;
import com.trial_task.food_delivery_fee.exception.ForbiddenVehicleTypeException;
import com.trial_task.food_delivery_fee.exception.WeatherDataFetchException;
import com.trial_task.food_delivery_fee.model.DeliveryFee;
import com.trial_task.food_delivery_fee.model.WeatherData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

/**
 * Service class for calculating delivery fees.
 */
@Service
public class DeliveryFeeService {

    @Value("#{${fee.city.base}}")
    private Map<String, Map<String, Double>> cityBaseFees;

    private final WeatherDataService weatherDataService;
    private final ValidationService validationService;
    private final WeatherFeeService weatherFeeService;

    /**
     * Constructs a DeliveryFeeService object with the given dependencies.
     *
     * @param weatherDataService The service for managing weather data.
     * @param validationService  The service for validating delivery fee calculations.
     * @param weatherFeeService  The service for calculating additional fees based on weather conditions.
     */
    public DeliveryFeeService(WeatherDataService weatherDataService,
                              ValidationService validationService,
                              WeatherFeeService weatherFeeService) {
        this.weatherDataService = weatherDataService;
        this.validationService = validationService;
        this.weatherFeeService = weatherFeeService;
    }

    /**
     * Calculates the delivery fee for a specific city and vehicle type.
     *
     * @param city        The city where the delivery is to be made.
     * @param vehicleType The type of vehicle used for the delivery.
     * @return The calculated delivery fee.
     * @throws ForbiddenVehicleTypeException If the vehicle type is not allowed.
     * @throws ForbiddenCityException        If the city is not allowed.
     */
    public DeliveryFee calculateFee(String city, String vehicleType) throws ForbiddenVehicleTypeException, ForbiddenCityException, WeatherDataFetchException {
        // Validate the city and vehicle type and throw an exception if they are not allowed
        validationService.validateCityAndVehicleType(city, vehicleType);

        // Fetch the latest weather data for the city
        Optional<WeatherData> weatherData = weatherDataService.getLatest(city);

        // If the weather data for the city is unavailable, throw an exception
        if (weatherData.isEmpty()) {
            throw new WeatherDataFetchException("No weather data available for the city: " + city);
        }

        // Calculate the delivery fee based on the provided rules
        double baseFee = calculateBaseFee(city, vehicleType);
        double airTemperatureExtraFee = weatherFeeService.calculateAirTemperatureExtraFee(vehicleType, weatherData.get());
        double windSpeedExtraFee = weatherFeeService.calculateWindSpeedExtraFee(vehicleType, weatherData.get());
        double weatherPhenomenonExtraFee = weatherFeeService.calculateWeatherPhenomenonExtraFee(vehicleType, weatherData.get());

        return new DeliveryFee(
                city,
                vehicleType,
                baseFee,
                airTemperatureExtraFee,
                windSpeedExtraFee,
                weatherPhenomenonExtraFee);
    }

    /**
     * Calculates the base fee for a specific city and vehicle type.
     *
     * @param city        The city where the delivery is to be made.
     * @param vehicleType The type of vehicle used for the delivery.
     * @return The base fee.
     */
    private double calculateBaseFee(String city, String vehicleType) {
        return cityBaseFees.get(city).get(vehicleType);
    }
}
