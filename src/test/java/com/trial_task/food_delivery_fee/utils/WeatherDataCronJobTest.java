package com.trial_task.food_delivery_fee.utils;

import com.trial_task.food_delivery_fee.service.WeatherDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
public class WeatherDataCronJobTest {

    @Autowired
    private WeatherDataCronJob weatherDataCronJob;

    @MockBean
    private RestTemplate restTemplate;

    @MockBean
    private WeatherDataService weatherDataService;

    //TODO Add Tests
}

