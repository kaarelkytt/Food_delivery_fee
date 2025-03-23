package com.trial_task.food_delivery_fee.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDateTime;

/**
 * Represents weather data for a specific station at a specific time.
 */
@Entity
@XmlRootElement(name = "station")
public class WeatherData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @XmlElement(name = "name")
    private String stationName;

    @XmlElement(name = "wmocode")
    private String WMOCode;

    @XmlElement(name = "airtemperature")
    private double airTemperature;

    @XmlElement(name = "windspeed")
    private double windSpeed;

    @XmlElement(name = "phenomenon")
    private String weatherPhenomenon;

    private LocalDateTime observationTimestamp;

    /**
     * Default constructor for creating a WeatherData object.
     */
    public WeatherData() {
    }

    /**
     * Constructs a WeatherData object with the given parameters.
     *
     * @param stationName          The name of the weather station.
     * @param WMOCode              The WMO code of the weather station.
     * @param airTemperature       The air temperature at the weather station.
     * @param windSpeed            The wind speed at the weather station.
     * @param weatherPhenomenon    The weather phenomenon observed at the weather station.
     * @param observationTimestamp The timestamp of the weather observation.
     */
    public WeatherData(String stationName, String WMOCode, double airTemperature, double windSpeed, String weatherPhenomenon, LocalDateTime observationTimestamp) {
        this.stationName = stationName;
        this.WMOCode = WMOCode;
        this.airTemperature = airTemperature;
        this.windSpeed = windSpeed;
        this.weatherPhenomenon = weatherPhenomenon;
        this.observationTimestamp = observationTimestamp;
    }

    public String getStationName() {
        return stationName;
    }

    public double getAirTemperature() {
        return airTemperature;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public String getWeatherPhenomenon() {
        return weatherPhenomenon;
    }

    public LocalDateTime getObservationTimestamp() {
        return observationTimestamp;
    }

    public void setObservationTimestamp(LocalDateTime observationTimestamp) {
        this.observationTimestamp = observationTimestamp;
    }

    @Override
    public String toString() {
        return "WeatherData{" +
                "id=" + id +
                ", stationName='" + stationName + '\'' +
                ", WMOCode='" + WMOCode + '\'' +
                ", airTemperature=" + airTemperature +
                ", windSpeed=" + windSpeed +
                ", weatherPhenomenon='" + weatherPhenomenon + '\'' +
                ", observationTimestamp=" + observationTimestamp +
                '}';
    }
}
