package com.trial_task.food_delivery_fee.service;

import com.trial_task.food_delivery_fee.exception.ForbiddenCityException;
import com.trial_task.food_delivery_fee.exception.ForbiddenVehicleTypeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ValidationServiceTest {

    private ValidationService validationService;

    @BeforeEach
    public void setUp() {
        validationService = new ValidationService();
        ReflectionTestUtils.setField(validationService, "cityBaseFees",
                Map.of("Tallinn", Map.of("car", 4.0, "scooter", 3.5, "bike", 3.0),
                        "Tartu", Map.of("car", 3.5, "scooter", 3.0, "bike", 2.5)));
        ReflectionTestUtils.setField(validationService, "cityToStationMap",
                Map.of("Tallinn", "Tallinn-Harku",
                        "Tartu", "Tartu-Tõravere",
                        "Pärnu", "Pärnu"));
    }

    @Test
    void testValidateCityAndVehicleType_validInputs_noExceptionThrown() {
        assertDoesNotThrow(() -> validationService.validateCityAndVehicleType("Tallinn", "car"));
        assertDoesNotThrow(() -> validationService.validateCityAndVehicleType("Tartu", "bike"));
    }

    @Test
    void testValidateCityAndVehicleType_ShouldThrowForbiddenCityException() {
        String city = "UnknownCity";
        String vehicleType = "car";

        assertThrows(ForbiddenCityException.class, () -> validationService.validateCityAndVehicleType(city, vehicleType));
    }

    @Test
    void testValidateCityAndVehicleType_ShouldThrowForbiddenVehicleTypeException() {
        String city = "Tallinn";
        String vehicleType = "unknownVehicle";

        assertThrows(ForbiddenVehicleTypeException.class, () -> validationService.validateCityAndVehicleType(city, vehicleType));
    }
}
