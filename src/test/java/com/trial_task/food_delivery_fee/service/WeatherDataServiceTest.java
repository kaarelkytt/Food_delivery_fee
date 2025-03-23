package com.trial_task.food_delivery_fee.service;

import com.trial_task.food_delivery_fee.exception.WeatherDataFetchException;
import com.trial_task.food_delivery_fee.exception.WeatherDataParsingException;
import com.trial_task.food_delivery_fee.model.WeatherData;
import com.trial_task.food_delivery_fee.repository.WeatherDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class WeatherDataServiceTest {

    @Mock
    private WeatherDataRepository weatherDataRepository;

    @Mock
    private WeatherDataFetcher weatherDataFetcher;

    @InjectMocks
    private WeatherDataService weatherDataService;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(weatherDataService, "cityToStationMap", Map.of("Tallinn", "Tallinn-Harku"));
    }

    @Test
    public void testGetLatest() {
        WeatherData weatherData = new WeatherData("Tallinn-Harku", "26038", 2.1, 3.2, "Overcast", LocalDateTime.now());
        when(weatherDataRepository.findFirstByStationNameOrderByObservationTimestampDesc("Tallinn-Harku"))
                .thenReturn(Optional.of(weatherData));

        Optional<WeatherData> result = weatherDataService.getLatest("Tallinn");

        assertTrue(result.isPresent());
        assertEquals(result.get(), weatherData);
    }

    @Test
    public void testUpdateWeatherData_Success() throws WeatherDataParsingException, WeatherDataFetchException {
        WeatherData weatherData = new WeatherData("Tallinn-Harku", "26038", 2.1, 3.2, "Overcast", LocalDateTime.now());
        when(weatherDataFetcher.fetchWeatherData()).thenReturn(List.of(weatherData));

        weatherDataService.updateWeatherData();

        verify(weatherDataRepository, times(1)).saveAll(anyList());
    }

    @Test
    public void testUpdateWeatherData_Failure() throws WeatherDataParsingException, WeatherDataFetchException {
        when(weatherDataFetcher.fetchWeatherData()).thenThrow(WeatherDataFetchException.class);

        assertThrows(WeatherDataFetchException.class, () -> weatherDataService.updateWeatherData());
    }
}
