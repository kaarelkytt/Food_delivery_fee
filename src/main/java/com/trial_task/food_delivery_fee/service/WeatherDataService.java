package com.trial_task.food_delivery_fee.service;

import com.trial_task.food_delivery_fee.exception.WeatherDataFetchException;
import com.trial_task.food_delivery_fee.exception.WeatherDataParsingException;
import com.trial_task.food_delivery_fee.model.WeatherData;
import com.trial_task.food_delivery_fee.repository.WeatherDataRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service class for managing weather data.
 */
@Service
public class WeatherDataService {
    private static final Logger log = LoggerFactory.getLogger(WeatherDataService.class);
    private final WeatherDataRepository weatherDataRepository;
    private final WeatherDataFetcher weatherDataFetcher;

    @Value("#{${weatherdata.cityToStationMap}}")
    private Map<String, String> cityToStationMap;

    /**
     * Constructor for the WeatherDataService class.
     *
     * @param weatherDataRepository The repository for storing weather data.
     * @param weatherDataFetcher    The service for fetching weather data.
     */
    public WeatherDataService(WeatherDataRepository weatherDataRepository,
                              WeatherDataFetcher weatherDataFetcher) {
        this.weatherDataRepository = weatherDataRepository;
        this.weatherDataFetcher = weatherDataFetcher;
    }

    /**
     * Fetches the latest weather data for a specific city from the database.
     *
     * @param city The city for which to fetch the latest weather data.
     * @return The latest weather data for the city, or null if there is no data for the city.
     */
    public Optional<WeatherData> getLatest(String city) {
        String stationName = cityToStationMap.get(city);
        return weatherDataRepository.findFirstByStationNameOrderByObservationTimestampDesc(stationName);
    }

    /**
     * Updates the weather data by fetching new data from the API and saving it to the database.
     *
     * @throws WeatherDataParsingException If there is an error parsing the weather data.
     * @throws WeatherDataFetchException   If there is an error fetching the weather data.
     */
    public void updateWeatherData() throws WeatherDataParsingException, WeatherDataFetchException {
        List<WeatherData> weatherDataList = weatherDataFetcher.fetchWeatherData();
        weatherDataRepository.saveAll(weatherDataList);
    }

    /**
     * Initializes the weather data by fetching it once at startup.
     */
    @PostConstruct
    public void initializeWeatherData() {
        log.info("Fetching initial weather data...");
        try {
            updateWeatherData();
            log.info("Initial weather data fetch successful");
        } catch (WeatherDataParsingException | WeatherDataFetchException e) {
            log.error(e.getMessage());
        }
    }
}
