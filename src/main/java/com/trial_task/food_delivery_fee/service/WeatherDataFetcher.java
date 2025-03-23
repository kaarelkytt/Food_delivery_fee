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

@Service
public class WeatherDataFetcher {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("#{${weatherdata.cityToStationMap}}")
    private Map<String, String> cityToStationMap;

    @Value("${weatherdata.api.url}")
    private String url;

    public List<WeatherData> fetchWeatherData() throws WeatherDataParsingException, WeatherDataFetchException {
        try {
            // Fetch the weather data from the API
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                String xmlData = response.getBody();

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
     */
    private List<WeatherData> parseWeatherData(String xmlData) throws WeatherDataParsingException {
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
