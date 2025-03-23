package com.trial_task.food_delivery_fee.service;

import com.trial_task.food_delivery_fee.exception.WeatherDataFetchException;
import com.trial_task.food_delivery_fee.exception.WeatherDataParsingException;
import com.trial_task.food_delivery_fee.model.WeatherData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@SpringBootTest
public class WeatherDataFetcherTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private WeatherDataFetcher weatherDataFetcher;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(weatherDataFetcher, "cityToStationMap", Map.of("Tallinn", "Tallinn-Harku"));
        ReflectionTestUtils.setField(weatherDataFetcher, "url", "https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php");
    }

    @Test
    public void testFetchWeatherData_Success() throws WeatherDataParsingException, WeatherDataFetchException {
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(getXmlResponse(), HttpStatus.OK));

        List<WeatherData> fetchedData = weatherDataFetcher.fetchWeatherData();

        assertNotNull(fetchedData);
        assertEquals(1, fetchedData.size());

        WeatherData fetchedWeatherData = fetchedData.getFirst();

        assertEquals("Tallinn-Harku", fetchedWeatherData.getStationName());
        assertEquals(3.8, fetchedWeatherData.getAirTemperature());
        assertEquals(2.8, fetchedWeatherData.getWindSpeed());
        assertEquals("Variable clouds", fetchedWeatherData.getWeatherPhenomenon());
        assertEquals(Instant.ofEpochSecond(1711285872).atZone(ZoneId.systemDefault()).toLocalDateTime(), fetchedWeatherData.getObservationTimestamp());
    }

    @Test
    public void testFetchWeatherData_FetchException() {
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenThrow(new RestClientException("API request failed"));

        assertThrows(WeatherDataFetchException.class, () -> weatherDataFetcher.fetchWeatherData());
    }

    @Test
    public void testParseWeatherData_ParsingException() {
        String invalidXmlData = "<invalidXml>";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(invalidXmlData, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(responseEntity);

        assertThrows(WeatherDataParsingException.class, () -> weatherDataFetcher.fetchWeatherData());
    }


    private String getXmlResponse() {
        return """
                <observations timestamp="1711285872">
                <station>
                <name>Kuressaare linn</name>
                <wmocode/>
                <longitude>22.48944444411111</longitude>
                <latitude>58.26416666666667</latitude>
                <phenomenon/>
                <visibility/>
                <precipitations/>
                <airpressure/>
                <relativehumidity>67</relativehumidity>
                <airtemperature>7.3</airtemperature>
                <winddirection/>
                <windspeed/>
                <windspeedmax/>
                <waterlevel/>
                <waterlevel_eh2000/>
                <watertemperature/>
                <uvindex/>
                <sunshineduration/>
                <globalradiation/>
                </station>
                <station>
                <name>Tallinn-Harku</name>
                <wmocode>26038</wmocode>
                <longitude>24.602891666624284</longitude>
                <latitude>59.398122222355134</latitude>
                <phenomenon>Variable clouds</phenomenon>
                <visibility>35.0</visibility>
                <precipitations>0</precipitations>
                <airpressure>996.7</airpressure>
                <relativehumidity>84</relativehumidity>
                <airtemperature>3.8</airtemperature>
                <winddirection>235</winddirection>
                <windspeed>2.8</windspeed>
                <windspeedmax>4.7</windspeedmax>
                <waterlevel/>
                <waterlevel_eh2000/>
                <watertemperature/>
                <uvindex>0.6</uvindex>
                <sunshineduration>0</sunshineduration>
                <globalradiation>115</globalradiation>
                </station>
                </observations>""";
    }
}

