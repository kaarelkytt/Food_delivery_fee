package com.trial_task.food_delivery_fee.service;

import com.trial_task.food_delivery_fee.exception.WeatherDataFetchException;
import com.trial_task.food_delivery_fee.model.DeliveryFee;
import com.trial_task.food_delivery_fee.model.WeatherData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
public class DeliveryFeeServiceTest {

    @Mock
    private WeatherDataService weatherDataService;

    @Mock
    private ValidationService validationService;

    @Mock
    private WeatherFeeService weatherFeeService;

    @InjectMocks
    private DeliveryFeeService deliveryFeeService;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(deliveryFeeService, "cityBaseFees",
                Map.of("Tallinn", Map.of("car", 4.0, "scooter", 3.5, "bike", 3.0),
                        "Tartu", Map.of("car", 3.5, "scooter", 3.0, "bike", 2.5)));
    }

    @Test
    void testCalculateFee_ShouldReturnCorrectFee() throws Exception {
        String city = "Tallinn";
        String vehicleType = "car";

        WeatherData mockWeatherData = new WeatherData();

        when(weatherDataService.getLatest(city)).thenReturn(Optional.of(mockWeatherData));
        when(weatherFeeService.calculateAirTemperatureExtraFee(vehicleType, mockWeatherData)).thenReturn(0.5);
        when(weatherFeeService.calculateWindSpeedExtraFee(vehicleType, mockWeatherData)).thenReturn(0.7);
        when(weatherFeeService.calculateWeatherPhenomenonExtraFee(vehicleType, mockWeatherData)).thenReturn(1.0);

        DeliveryFee result = deliveryFeeService.calculateFee(city, vehicleType);

        assertEquals(4.0, result.getRegionalBaseFee());
        assertEquals(0.5, result.getAirTemperatureExtraFee());
        assertEquals(0.7, result.getWindSpeedExtraFee());
        assertEquals(1.0, result.getWeatherPhenomenonExtraFee());
        assertEquals(6.2, result.getTotalFee());
    }

    @Test
    void testCalculateFee_ShouldThrowWeatherDataFetchException_WhenNoWeatherData() {
        String city = "Tallinn";
        String vehicleType = "car";

        when(weatherDataService.getLatest(city)).thenReturn(Optional.empty());

        assertThrows(WeatherDataFetchException.class, () -> deliveryFeeService.calculateFee(city, vehicleType));
    }
}
