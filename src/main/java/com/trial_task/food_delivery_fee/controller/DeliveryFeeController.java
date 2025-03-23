package com.trial_task.food_delivery_fee.controller;

import com.trial_task.food_delivery_fee.exception.ForbiddenCityException;
import com.trial_task.food_delivery_fee.exception.ForbiddenVehicleTypeException;
import com.trial_task.food_delivery_fee.exception.WeatherDataFetchException;
import com.trial_task.food_delivery_fee.model.DeliveryFee;
import com.trial_task.food_delivery_fee.model.DeliveryFeeResponse;
import com.trial_task.food_delivery_fee.service.DeliveryFeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling delivery fee related requests.
 */
@RestController
@RequestMapping("/deliveryFee")
public class DeliveryFeeController {

    private final DeliveryFeeService deliveryFeeService;

    public DeliveryFeeController(DeliveryFeeService deliveryFeeService) {
        this.deliveryFeeService = deliveryFeeService;
    }

    /**
     * Calculates the delivery fee based on the city and vehicle type.
     *
     * @param city        The city where the delivery is to be made.
     * @param vehicleType The type of vehicle used for the delivery.
     * @return The calculated delivery fee or an error message if the vehicle type is forbidden or
     * when vehicle type or city is not supported.
     */
    @GetMapping
    public ResponseEntity<DeliveryFeeResponse> calculateDeliveryFee(@RequestParam String city, @RequestParam String vehicleType) {
        try {
            DeliveryFee deliveryFee = deliveryFeeService.calculateFee(city, vehicleType);
            return ResponseEntity.ok(new DeliveryFeeResponse("OK", deliveryFee));
        } catch (ForbiddenVehicleTypeException | ForbiddenCityException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new DeliveryFeeResponse("ERROR - " + e.getMessage()));
        } catch (WeatherDataFetchException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new DeliveryFeeResponse("ERROR - " + e.getMessage()));
        }
    }
}
