package com.trial_task.food_delivery_fee.service;

import com.trial_task.food_delivery_fee.exception.ForbiddenCityException;
import com.trial_task.food_delivery_fee.exception.ForbiddenVehicleTypeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ValidationService {

    @Value("#{${weatherdata.cityToStationMap}}")
    private Map<String, String> cityToStationMap;

    @Value("#{${fee.city.base}}")
    private Map<String, Map<String, Double>> cityBaseFees;

    /**
     * Validates the city and vehicle type.
     *
     * @param city The city to validate.
     * @param vehicleType The vehicle type to validate.
     * @throws ForbiddenVehicleTypeException If the vehicle type is not allowed.
     * @throws ForbiddenCityException If the city is not allowed.
     */
    public void validateCityAndVehicleType(String city, String vehicleType) throws ForbiddenCityException, ForbiddenVehicleTypeException {
        if (!cityToStationMap.containsKey(city) && !cityBaseFees.containsKey(city)) {
            throw new ForbiddenCityException("Invalid city: " + city);
        }

        if (!cityBaseFees.get(city).containsKey(vehicleType)) {
            throw new ForbiddenVehicleTypeException("Invalid vehicle type: " + vehicleType);
        }
    }
}
