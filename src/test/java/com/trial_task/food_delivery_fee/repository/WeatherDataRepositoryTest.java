package com.trial_task.food_delivery_fee.repository;

import com.trial_task.food_delivery_fee.model.WeatherData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class WeatherDataRepositoryTest {

    @Autowired
    private WeatherDataRepository weatherDataRepository;

    @Test
    void testFindFirst_ShouldReturnLatestWeatherData() {
        LocalDateTime fixedTime = LocalDateTime.of(2025, 3, 23, 18, 17, 52);
        WeatherData oldData1 = new WeatherData("Tallinn-Harku", "26038", 2.1, 3.2, "Overcast", fixedTime.minusHours(2));
        WeatherData oldData2 = new WeatherData("Tallinn-Harku", "26038", 3.1, 2.2, "Overcast", fixedTime.minusHours(23));
        WeatherData latestData  = new WeatherData("Tallinn-Harku", "26038", 4.7, 1.8, "Overcast", fixedTime);

        weatherDataRepository.saveAll(List.of(oldData1, latestData, oldData2));

        Optional<WeatherData> result = weatherDataRepository.findFirstByStationNameOrderByObservationTimestampDesc("Tallinn-Harku");

        assertTrue(result.isPresent());
        assertEquals(latestData.getObservationTimestamp(), result.get().getObservationTimestamp());
        assertEquals(latestData.getAirTemperature(), result.get().getAirTemperature());
    }

    @Test
    void testFindFirst_ShouldReturnEmpty_WhenNoData() {
        Optional<WeatherData> result = weatherDataRepository.findFirstByStationNameOrderByObservationTimestampDesc("Tallinn-Harku");

        assertTrue(result.isEmpty());
    }
}
