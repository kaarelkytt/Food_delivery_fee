package com.trial_task.food_delivery_fee.service;

import com.trial_task.food_delivery_fee.repository.WeatherDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class WeatherDataServiceTest {

    @Autowired
    private WeatherDataService weatherDataService;

    @MockBean
    private WeatherDataRepository weatherDataRepository;

    //TODO Add Tests
}
