package com.trial_task.food_delivery_fee.controller;

import com.trial_task.food_delivery_fee.exception.ForbiddenCityException;
import com.trial_task.food_delivery_fee.exception.ForbiddenVehicleTypeException;
import com.trial_task.food_delivery_fee.exception.WeatherDataFetchException;
import com.trial_task.food_delivery_fee.model.DeliveryFee;
import com.trial_task.food_delivery_fee.service.DeliveryFeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class DeliveryFeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeliveryFeeService deliveryFeeService;

    @Test
    void testCalculateDeliveryFee_ShouldReturnOkResponse() throws Exception {
        String city = "Tallinn";
        String vehicleType = "car";

        DeliveryFee deliveryFee = new DeliveryFee(city, vehicleType, 4.0, 0.5, 1.0, 0.3);
        when(deliveryFeeService.calculateFee(city, vehicleType)).thenReturn(deliveryFee);

        mockMvc.perform(get("/deliveryFee")
                        .param("city", city)
                        .param("vehicleType", vehicleType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.deliveryFee.city").value(city))
                .andExpect(jsonPath("$.deliveryFee.vehicleType").value(vehicleType))
                .andExpect(jsonPath("$.deliveryFee.regionalBaseFee").value(4.0))
                .andExpect(jsonPath("$.deliveryFee.airTemperatureExtraFee").value(0.5))
                .andExpect(jsonPath("$.deliveryFee.windSpeedExtraFee").value(1.0))
                .andExpect(jsonPath("$.deliveryFee.weatherPhenomenonExtraFee").value(0.3))
                .andExpect(jsonPath("$.deliveryFee.totalFee").value(5.8));
    }

    @Test
    void testCalculateDeliveryFee_ShouldReturnBadRequest_WhenVehicleForbidden() throws Exception {
        String city = "Tallinn";
        String vehicleType = "unknownVehicle";

        when(deliveryFeeService.calculateFee(city, vehicleType))
                .thenThrow(new ForbiddenVehicleTypeException("Vehicle type not supported"));

        mockMvc.perform(get("/deliveryFee")
                        .param("city", city)
                        .param("vehicleType", vehicleType))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("ERROR - Vehicle type not supported"));
    }

    @Test
    void testCalculateDeliveryFee_ShouldReturnBadRequest_WhenCityForbidden() throws Exception {
        String city = "UnknownCity";
        String vehicleType = "car";

        when(deliveryFeeService.calculateFee(city, vehicleType))
                .thenThrow(new ForbiddenCityException("City not supported"));

        mockMvc.perform(get("/deliveryFee")
                        .param("city", city)
                        .param("vehicleType", vehicleType))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("ERROR - City not supported"));
    }

    @Test
    void testCalculateDeliveryFee_ShouldReturnServiceUnavailable_WhenWeatherDataUnavailable() throws Exception {
        String city = "Tallinn";
        String vehicleType = "car";

        when(deliveryFeeService.calculateFee(city, vehicleType))
                .thenThrow(new WeatherDataFetchException("No weather data available"));

        mockMvc.perform(get("/deliveryFee")
                        .param("city", city)
                        .param("vehicleType", vehicleType))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value("ERROR - No weather data available"));
    }

}
