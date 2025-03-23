package com.trial_task.food_delivery_fee.service;

import com.trial_task.food_delivery_fee.exception.WeatherDataFetchException;
import com.trial_task.food_delivery_fee.exception.WeatherDataParsingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * A service class that fetches weather data from an API at regular intervals and stores it in the database.
 */
@Service
public class WeatherDataCronJob {
    private static final Logger log = LoggerFactory.getLogger(WeatherDataCronJob.class);
    private final WeatherDataService weatherDataService;

    /**
     * Constructor for the WeatherDataCronJob class.
     *
     * @param weatherDataService The service for handling weather data.
     */
    public WeatherDataCronJob(WeatherDataService weatherDataService) {
        this.weatherDataService = weatherDataService;
    }

    /**
     * Fetches weather data from the API and stores it in the database.
     * This method is scheduled to run at regular intervals specified by the cron expression.
     */
    @Scheduled(cron = "${weatherdata.cron.expression}")
    public void fetchAndStoreWeatherData() {
        try {
            weatherDataService.updateWeatherData();
            log.info("Weather data update successful");
        } catch (WeatherDataParsingException | WeatherDataFetchException e) {
            log.error(e.getMessage());
        }
    }
}

