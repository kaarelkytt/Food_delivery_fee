package com.trial_task.food_delivery_fee.service;

import com.trial_task.food_delivery_fee.exception.WeatherDataFetchException;
import com.trial_task.food_delivery_fee.exception.WeatherDataParsingException;
import com.trial_task.food_delivery_fee.model.Observations;
import com.trial_task.food_delivery_fee.model.WeatherData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
 * Service class for fetching weather data from an API.
 */
@Service
public class WeatherDataFetcher {
    private final RestTemplate restTemplate;

    @Value("#{${weatherdata.cityToStationMap}}")
    private Map<String, String> cityToStationMap;

    @Value("${weatherdata.api.url}")
    private String url;

    /**
     * Default constructor that initializes RestTemplate.
     */
    public WeatherDataFetcher() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Constructor for injecting a custom RestTemplate.
     *
     * @param restTemplate The RestTemplate to be used for API calls.
     */
    public WeatherDataFetcher(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    /**
     * Fetches weather data from the API.
     *
     * @return A list of WeatherData objects.
     * @throws WeatherDataParsingException If there is an error parsing the weather data.
     * @throws WeatherDataFetchException   If there is an error fetching the weather data.
     */
    public List<WeatherData> fetchWeatherData() throws WeatherDataParsingException, WeatherDataFetchException {
        try {
            // Fetch the weather data from the API
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                String xmlData = response.getBody();

                // Parse the XML data into WeatherData objects
                return parseWeatherData(xmlData);
            } else {
                throw new WeatherDataFetchException("Failed to fetch weather data: " + response.getStatusCode());
            }
        } catch (RestClientException e) {
            throw new WeatherDataFetchException("Error fetching weather data");
        }
    }

    /**
     * Parses the XML weather data and converts it into a list of WeatherData objects.
     *
     * @param xmlData The XML weather data.
     * @return A list of WeatherData objects.
     * @throws WeatherDataParsingException If there is an error parsing the weather data.
     */
    private List<WeatherData> parseWeatherData(String xmlData) throws WeatherDataParsingException {
        List<WeatherData> weatherDataList = new ArrayList<>();

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Observations.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            StringReader reader = new StringReader(xmlData);

            // Unmarshal the XML data into Observations object
            Observations observations = (Observations) unmarshaller.unmarshal(reader);

            // Convert epoch time to LocalDateTime
            long epochTime = Long.parseLong(observations.getTimestamp());
            LocalDateTime observationTimestamp = Instant
                    .ofEpochSecond(epochTime)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            // Add observation timestamp to each weather data object and add relevant data to the list
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
