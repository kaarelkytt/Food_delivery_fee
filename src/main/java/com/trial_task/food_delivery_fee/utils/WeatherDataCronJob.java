package com.trial_task.food_delivery_fee.utils;

import com.trial_task.food_delivery_fee.exception.WeatherDataFetchException;
import com.trial_task.food_delivery_fee.exception.WeatherDataParsingException;
import com.trial_task.food_delivery_fee.model.Observations;
import com.trial_task.food_delivery_fee.model.WeatherData;
import com.trial_task.food_delivery_fee.service.WeatherDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A service class that fetches weather data from an API at regular intervals and stores it in the database.
 */
@Service
public class WeatherDataCronJob {

    private static final Logger log = LoggerFactory.getLogger(WeatherDataCronJob.class);
    private final WeatherDataService weatherDataService;
    private final RestTemplate restTemplate;

    @Value("#{${cityToStationMap}}")
    private Map<String, String> cityToStationMap;

    @Value("${weatherdata.api.url}")
    private String url;

    /**
     * Constructor for the WeatherDataCronJob class.
     *
     * @param weatherDataService The service for handling weather data.
     */
    public WeatherDataCronJob(WeatherDataService weatherDataService) {
        this.weatherDataService = weatherDataService;
        this.restTemplate = new RestTemplate();
    }

    /**
     * Fetches weather data from the API and stores it in the database.
     * This method is scheduled to run at regular intervals specified by the cron expression.
     */
    @Scheduled(cron = "${weatherdata.cron.expression}")
    public void fetchAndStoreWeatherData() {
        try {
            // Fetch the weather data from the API
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                String xmlData = response.getBody();

                // Parse the XML data and convert it into WeatherData objects
                List<WeatherData> weatherDataList = parseWeatherData(xmlData);

                // Store the WeatherData objects in the database
                weatherDataService.saveAll(weatherDataList);
            } else {
                // Handle the case where the API request was not successful
                throw new WeatherDataFetchException("Failed to fetch weather data: " + response.getStatusCode());
            }
        } catch (WeatherDataFetchException | WeatherDataParsingException | RestClientException e) {
            log.error("Error fetching and storing weather data", e);
            return;
        }

        log.info("Weather data update successful");
    }

    /**
     * Parses the XML weather data and converts it into a list of WeatherData objects.
     *
     * @param xmlData The XML weather data.
     * @return A list of WeatherData objects.
     */
    private List<WeatherData> parseWeatherData(String xmlData) {
        List<WeatherData> weatherDataList = new ArrayList<>();

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Observations.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            StringReader reader = new StringReader(xmlData);
            Observations observations = (Observations) unmarshaller.unmarshal(reader);

            long epochTime = Long.parseLong(observations.getTimestamp());
            LocalDateTime observationTimestamp = Instant
                    .ofEpochSecond(epochTime)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            for (WeatherData weatherData : observations.getStations()) {
                String name = weatherData.getStationName();

                if (cityToStationMap.containsValue(name)) {
                    weatherData.setObservationTimestamp(observationTimestamp);
                    weatherDataList.add(weatherData);
                }

            }
        } catch (JAXBException e) {
            throw new WeatherDataParsingException("Error parsing weather data");
        }

        return weatherDataList;
    }
}

