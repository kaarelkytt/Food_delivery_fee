package com.trial_task.food_delivery_fee.service;

import com.trial_task.food_delivery_fee.model.WeatherData;
import com.trial_task.food_delivery_fee.repository.WeatherDataRepository;
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
    private final WeatherDataRepository weatherDataRepository;

    @Value("#{${weatherdata.cityToStationMap}}")
    private Map<String, String> cityToStationMap;

    /**
     * Constructor for the WeatherDataService class.
     *
     * @param weatherDataRepository The repository for accessing weather data in the database.
     */
    public WeatherDataService(WeatherDataRepository weatherDataRepository) {
        this.weatherDataRepository = weatherDataRepository;
    }

    /**
     * Fetches all weather data from the database.
     *
     * @return A list of all weather data.
     */
    public List<WeatherData> getAllWeatherData() {
        return weatherDataRepository.findAll();
    }

    /**
     * Saves a weather data object to the database.
     *
     * @param weatherData The weather data object to save.
     */
    public void save(WeatherData weatherData) {
        weatherDataRepository.save(weatherData);
    }

    /**
     * Saves a list of weather data objects to the database.
     *
     * @param weatherDataList The list of weather data objects to save.
     */
    public void saveAll(List<WeatherData> weatherDataList) {
        weatherDataRepository.saveAll(weatherDataList);
    }

    /**
     * Fetches the latest weather data for a specific city from the database.
     *
     * @param city The city for which to fetch the latest weather data.
     * @return The latest weather data for the city, or null if there is no data for the city.
     */
    public Optional<WeatherData> getLatest(String city) {
        String stationName = cityToStationMap.get(city);

        // Return the latest weather data for the specified station
        return weatherDataRepository.findFirstByStationNameOrderByObservationTimestampDesc(stationName);
    }
}
