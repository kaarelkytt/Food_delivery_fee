package com.trial_task.food_delivery_fee.service;

import com.trial_task.food_delivery_fee.exception.ForbiddenCityException;
import com.trial_task.food_delivery_fee.exception.ForbiddenVehicleTypeException;
import com.trial_task.food_delivery_fee.exception.WeatherDataFetchException;
import com.trial_task.food_delivery_fee.model.DeliveryFee;
import com.trial_task.food_delivery_fee.model.WeatherData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Service class for calculating delivery fees.
 */
@Service
public class DeliveryFeeService {

    @Value("#{${city.baseFee}}")
    private Map<String, Map<String, Double>> cityBaseFees;

    @Value("#{${cityToStationMap}}")
    private Map<String, String> cityToStationMap;

    @Value("#{'${allowedVehicleTypes}'.split(',')}")
    private List<String> allowedVehicleTypes;

    @Autowired
    private WeatherDataService weatherDataService;

    /**
     * Calculates the delivery fee for a specific city and vehicle type.
     *
     * @param city The city where the delivery is to be made.
     * @param vehicleType The type of vehicle used for the delivery.
     * @return The calculated delivery fee.
     * @throws ForbiddenVehicleTypeException If the vehicle type is not allowed.
     * @throws ForbiddenCityException If the city is not allowed.
     */
    public DeliveryFee calculateFee(String city, String vehicleType) throws ForbiddenVehicleTypeException, ForbiddenCityException, WeatherDataFetchException {
        validateCityAndVehicleType(city, vehicleType);

        // Fetch the latest weather data for the city
        WeatherData weatherData = weatherDataService.getLatest(city);

        // If the weather data for the city is unavailable, throw an exception
        if (weatherData == null){
            throw new WeatherDataFetchException("No weather data available for the city: " + city);
        }

        // Calculate the delivery fee based on the provided rules
        double baseFee = calculateBaseFee(city, vehicleType);
        double airTemperatureExtraFee = calculateAirTemperatureExtraFee(vehicleType, weatherData);;
        double windSpeedExtraFee = calculateWindSpeedExtraFee(vehicleType, weatherData);
        double weatherPhenomenonExtraFee = calculateWeatherPhenomenonExtraFee(vehicleType, weatherData);

        return new DeliveryFee(
                city,
                vehicleType,
                baseFee,
                airTemperatureExtraFee,
                windSpeedExtraFee,
                weatherPhenomenonExtraFee);
    }

    /**
     * Validates the city and vehicle type.
     *
     * @param city The city to validate.
     * @param vehicleType The vehicle type to validate.
     * @throws ForbiddenVehicleTypeException If the vehicle type is not allowed.
     * @throws ForbiddenCityException If the city is not allowed.
     */
    private void validateCityAndVehicleType(String city, String vehicleType) {
        if (!cityToStationMap.containsKey(city)) {
            throw new ForbiddenCityException("Invalid city: " + city);
        }

        if (!allowedVehicleTypes.contains(vehicleType)) {
            throw new ForbiddenVehicleTypeException("Invalid vehicle type: " + vehicleType);
        }
    }

    /**
     * Calculates the base fee for a specific city and vehicle type.
     *
     * @param city The city where the delivery is to be made.
     * @param vehicleType The type of vehicle used for the delivery.
     * @return The base fee.
     */
    private double calculateBaseFee(String city, String vehicleType) {
        return cityBaseFees.get(city).get(vehicleType);
    }

    /**
     * Calculates the extra fee based on air temperature for a specific vehicle type.
     *
     * @param vehicleType The type of vehicle used for the delivery.
     * @param weatherData The weather data.
     * @return The extra fee based on air temperature.
     */
    private double calculateAirTemperatureExtraFee(String vehicleType, WeatherData weatherData) {
        double fee = 0;

        if (vehicleType.equals("scooter") || vehicleType.equals("bike")){
            if (weatherData.getAirTemperature() < -10){
                fee = 1;
            } else if (weatherData.getAirTemperature() < 0) {
                fee = 0.5;
            }
        }

        return fee;
    }

    /**
     * Calculates the extra fee based on wind speed for a specific vehicle type.
     *
     * @param vehicleType The type of vehicle used for the delivery.
     * @param weatherData The weather data.
     * @return The extra fee based on wind speed.
     * @throws ForbiddenVehicleTypeException If the vehicle type is not allowed due to high wind speed.
     */
    private double calculateWindSpeedExtraFee(String vehicleType, WeatherData weatherData) {
        double fee = 0;

        if (vehicleType.equals("bike")){
            if (weatherData.getWindSpeed() > 20){
                throw new ForbiddenVehicleTypeException(
                        "Usage of selected vehicle type is forbidden");
            } else if (weatherData.getWindSpeed() > 10) {
                fee = 0.5;
            }
        }

        return fee;
    }

    /**
     * Calculates the extra fee based on weather phenomenon for a specific vehicle type.
     *
     * @param vehicleType The type of vehicle used for the delivery.
     * @param weatherData The weather data.
     * @return The extra fee based on weather phenomenon.
     * @throws ForbiddenVehicleTypeException If the vehicle type is not allowed due to certain weather conditions.
     */
    private double calculateWeatherPhenomenonExtraFee(String vehicleType, WeatherData weatherData) {
        double fee = 0;

        if (vehicleType.equals("scooter") || vehicleType.equals("bike")){
            String weatherPhenomenon = weatherData.getWeatherPhenomenon().toLowerCase();
            if (weatherPhenomenon.contains("snow") ||
                    weatherPhenomenon.contains("sleet")){
                fee = 1;
            } else if (weatherPhenomenon.contains("rain") ||
                    weatherPhenomenon.contains("shower")) {
                fee = 0.5;
            } else if (weatherPhenomenon.contains("glaze") ||
                    weatherPhenomenon.contains("hail") ||
                    weatherPhenomenon.contains("thunder")) {
                throw new ForbiddenVehicleTypeException(
                        "Usage of selected vehicle type is forbidden");
            }
        }

        return fee;
    }
}
