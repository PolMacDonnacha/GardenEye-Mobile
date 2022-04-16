package com.example.plantmonitor;

public class Plot {
    Float Temperature;
    Float Humidity;
    Integer SoilMoisture;
    Integer LightLevel;
    String TimeWatered;

    public Float getTemperature() {
        return Temperature;
    }

    public void setTemperature(Float temperature) {
        Temperature = temperature;
    }

    public Float getHumidity() {
        return Humidity;
    }

    public void setHumidity(Float humidity) {
        Humidity = humidity;
    }

    public Integer getSoilMoisture() {
        return SoilMoisture;
    }

    public void setSoilMoisture(Integer soilMoisture) {
        SoilMoisture = soilMoisture;
    }

    public Integer getLightLevel() {
        return LightLevel;
    }

    public void setLightLevel(Integer lightLevel) {
        LightLevel = lightLevel;
    }

    public String getTimeWatered() {
        return TimeWatered;
    }

    public void setTimeWatered(String timeWatered) {
        TimeWatered = timeWatered;
    }

    public Plot() {
    }

    public Plot(Float humidity, Integer lightLevel, Integer soilMoisture, Float temperature, String timeWatered) {
        Humidity = humidity;
        LightLevel = lightLevel;
        SoilMoisture = soilMoisture;
        Temperature = temperature;
        TimeWatered = timeWatered;

    }

}
