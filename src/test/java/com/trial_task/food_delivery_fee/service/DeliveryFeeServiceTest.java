package com.trial_task.food_delivery_fee.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class DeliveryFeeServiceTest {

    @Autowired
    private DeliveryFeeService deliveryFeeService;

    @MockBean
    private WeatherDataService weatherDataService;

    //TODO Add Tests
}
