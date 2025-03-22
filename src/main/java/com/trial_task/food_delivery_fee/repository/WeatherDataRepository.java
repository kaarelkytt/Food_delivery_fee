package com.trial_task.food_delivery_fee.repository;

import com.trial_task.food_delivery_fee.model.WeatherData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for accessing WeatherData in the database.
 */
@Repository
public interface WeatherDataRepository extends JpaRepository<WeatherData, Long> {

    /**
     * Fetches the latest weather data for a specific station from the database.
     *
     * @param stationName The name of the station.
     * @return The latest weather data for the specified station.
     */
    Optional<WeatherData> findFirstByStationNameOrderByObservationTimestampDesc(String stationName);
}
