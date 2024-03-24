package com.trial_task.food_delivery_fee.repository;

import com.trial_task.food_delivery_fee.model.WeatherData;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for accessing WeatherData in the database.
 */
@Repository
public interface WeatherDataRepository extends JpaRepository<WeatherData, Long> {

    /**
     * Finds the latest weather data for a specific station.
     *
     * @param stationName The name of the station.
     * @param pageable A Pageable object to specify the number of records to fetch.
     * @return A list of the latest WeatherData for the station.
     */
    @Query("SELECT wd FROM WeatherData wd WHERE wd.stationName = :stationName ORDER BY wd.observationTimestamp DESC")
    List<WeatherData> findLatestByStationName(@Param("stationName") String stationName, Pageable pageable);
}
