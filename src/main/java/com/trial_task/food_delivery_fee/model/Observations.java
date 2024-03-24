package com.trial_task.food_delivery_fee.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Represents a collection of weather data observations.
 */
@XmlRootElement(name = "observations")
public class Observations {

    @XmlAttribute
    private String timestamp;

    @XmlElement(name = "station")
    private List<WeatherData> stations;

    public String getTimestamp() {
        return timestamp;
    }

    public List<WeatherData> getStations() {
        return stations;
    }
}
