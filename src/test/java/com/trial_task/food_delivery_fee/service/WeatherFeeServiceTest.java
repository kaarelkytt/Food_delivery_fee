package com.trial_task.food_delivery_fee.service;

import com.trial_task.food_delivery_fee.exception.ForbiddenVehicleTypeException;
import com.trial_task.food_delivery_fee.model.WeatherData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@SpringBootTest
class WeatherFeeServiceTest {

    @InjectMocks
    private WeatherFeeService weatherFeeService;

    @Mock
    private WeatherData weatherData;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(weatherFeeService, "temperatureApplicableVehicles", List.of("bike", "scooter"));
        ReflectionTestUtils.setField(weatherFeeService, "temperatureThresholds", List.of(-10.0, 0.0));
        ReflectionTestUtils.setField(weatherFeeService, "temperatureFees", List.of(1.0, 0.5));

        ReflectionTestUtils.setField(weatherFeeService, "windSpeedApplicableVehicles", List.of("bike"));
        ReflectionTestUtils.setField(weatherFeeService, "maxAllowedWindSpeed", 20.0);
        ReflectionTestUtils.setField(weatherFeeService, "windSpeedThresholds", List.of(10.0));
        ReflectionTestUtils.setField(weatherFeeService, "windSpeedFees", List.of(0.5));

        ReflectionTestUtils.setField(weatherFeeService, "weatherPhenomenonApplicableVehicles", List.of("bike", "scooter"));
        ReflectionTestUtils.setField(weatherFeeService, "forbiddenWeatherPhenomena", List.of("glaze", "hail", "thunder"));
        ReflectionTestUtils.setField(weatherFeeService, "weatherPhenomenaTypes", List.of("snow","sleet","rain","shower"));
        ReflectionTestUtils.setField(weatherFeeService, "weatherPhenomenaFees", List.of(1.0, 1.0, 0.5, 0.5));
    }

    @Test
    void testCalculateAirTemperatureExtraFee_ShouldReturnCorrectFee() {
        when(weatherData.getAirTemperature()).thenReturn(-5.0);
        double fee = weatherFeeService.calculateAirTemperatureExtraFee("bike", weatherData);
        assertEquals(0.5, fee);
    }

    @Test
    void testCalculateAirTemperatureExtraFee_ShouldReturnZeroForNonApplicableVehicle() {
        when(weatherData.getAirTemperature()).thenReturn(-5.0);
        double fee = weatherFeeService.calculateAirTemperatureExtraFee("car", weatherData);
        assertEquals(0, fee);
    }

    @Test
    void testCalculateWindSpeedExtraFee_ShouldReturnCorrectFee() throws ForbiddenVehicleTypeException {
        when(weatherData.getWindSpeed()).thenReturn(12.0);
        double fee = weatherFeeService.calculateWindSpeedExtraFee("bike", weatherData);
        assertEquals(0.5, fee);
    }

    @Test
    void testCalculateWindSpeedExtraFee_ShouldReturnZeroForNonApplicableVehicle() {
        when(weatherData.getWindSpeed()).thenReturn(25.0);
        double fee = weatherFeeService.calculateAirTemperatureExtraFee("scooter", weatherData);
        assertEquals(0, fee);
    }

    @Test
    void testCalculateWindSpeedExtraFee_ShouldThrowExceptionForHighWindSpeed() {
        when(weatherData.getWindSpeed()).thenReturn(25.0);
        assertThrows(ForbiddenVehicleTypeException.class, () -> weatherFeeService.calculateWindSpeedExtraFee("bike", weatherData));
    }

    @Test
    void testCalculateWeatherPhenomenonExtraFee_ShouldReturnCorrectFee() throws ForbiddenVehicleTypeException {
        when(weatherData.getWeatherPhenomenon()).thenReturn("Moderate snow shower");
        double fee = weatherFeeService.calculateWeatherPhenomenonExtraFee("bike", weatherData);
        assertEquals(1.0, fee);
    }

    @Test
    void testCalculateWeatherPhenomenonExtraFee_ShouldReturnZeroForNonApplicableVehicle() throws ForbiddenVehicleTypeException {
        when(weatherData.getWeatherPhenomenon()).thenReturn("Hail");
        double fee = weatherFeeService.calculateWeatherPhenomenonExtraFee("car", weatherData);
        assertEquals(0, fee);
    }

    @Test
    void testCalculateWeatherPhenomenonExtraFee_ShouldThrowExceptionForForbiddenWeather() {
        when(weatherData.getWeatherPhenomenon()).thenReturn("Glaze");
        assertThrows(ForbiddenVehicleTypeException.class, () -> weatherFeeService.calculateWeatherPhenomenonExtraFee("bike", weatherData));
    }
}
