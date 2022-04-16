package com.example.plantmonitor;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Controls {
    int autoCool;
    int autoPump;
    int autoTimelapse;
    int fanSwitch;
    int fanTime;
    int idealSoilMoisture;
    int maxTemp;
    int pumpSwitch;
    int pumpTime;
    int refreshInterval;
    int timelapseFps;
    float timelapseInterval;
    float timelapseLength;
    int timelapseSwitch;

    public Controls() {
    }

    public int getAutoCool() {
        return autoCool;
    }

    public void setAutoCool(int autoCool) {
        this.autoCool = autoCool;
    }

    public int getAutoPump() {
        return autoPump;
    }

    public void setAutoPump(int autoPump) {
        this.autoPump = autoPump;
    }

    public int getAutoTimelapse() {
        return autoTimelapse;
    }

    public void setAutoTimelapse(int autoTimelapse) {
        this.autoTimelapse = autoTimelapse;
    }

    public int getFanSwitch() {
        return fanSwitch;
    }

    public void setFanSwitch(int fanSwitch) {
        this.fanSwitch = fanSwitch;
    }

    public int getFanTime() {
        return fanTime;
    }

    public void setFanTime(int fanTime) {
        this.fanTime = fanTime;
    }

    public int getIdealSoilMoisture() {
        return idealSoilMoisture;
    }

    public void setIdealSoilMoisture(int idealSoilMoisture) {
        this.idealSoilMoisture = idealSoilMoisture;
    }

    public int getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(int maxTemp) {
        this.maxTemp = maxTemp;
    }

    public int getPumpSwitch() {
        return pumpSwitch;
    }

    public void setPumpSwitch(int pumpSwitch) {
        this.pumpSwitch = pumpSwitch;
    }

    public int getPumpTime() {
        return pumpTime;
    }

    public void setPumpTime(int pumpTime) {
        this.pumpTime = pumpTime;
    }

    public int getRefreshInterval() {
        return refreshInterval;
    }

    public void setRefreshInterval(int refreshInterval) {
        this.refreshInterval = refreshInterval;
    }

    public int getTimelapseFps() {
        return timelapseFps;
    }

    public void setTimelapseFps(int timelapseFps) {
        this.timelapseFps = timelapseFps;
    }

    public float getTimelapseInterval() {
        return timelapseInterval;
    }

    public void setTimelapseInterval(float timelapseInterval) {
        this.timelapseInterval = timelapseInterval;
    }

    public float getTimelapseLength() {
        return timelapseLength;
    }

    public void setTimelapseLength(float timelapseLength) {
        this.timelapseLength = timelapseLength;
    }


    public int getTimelapseSwitch() {
        return timelapseSwitch;
    }

    public void setTimelapseSwitch(int timelapseSwitch) {
        this.timelapseSwitch = timelapseSwitch;
    }

    public Controls(int autoCool, int autoPump, int autoTimelapse, int fanSwitch, int fanTime, int maxTemp, int pumpSwitch, int pumpTime, int timelapseFps, int timelapseInterval, int timelapseLength, int timelapseSwitch) {
        this.autoCool = autoCool;
        this.autoPump = autoPump;
        this.autoTimelapse = autoTimelapse;
        this.fanSwitch = fanSwitch;
        this.fanTime = fanTime;
        this.maxTemp = maxTemp;
        this.pumpSwitch = pumpSwitch;
        this.pumpTime = pumpTime;
        this.timelapseFps = timelapseFps;
        this.timelapseInterval = timelapseInterval;
        this.timelapseLength = timelapseLength;
        this.timelapseSwitch = timelapseSwitch;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("autoCool", autoCool);
        result.put("autoPump", autoPump);
        result.put("autoTimelapse", autoTimelapse);
        result.put("fanSwitch", fanSwitch);
        result.put("fanTime", fanTime);
        result.put("maxTemp", maxTemp);
        result.put("pumpSwitch", pumpSwitch);
        result.put("pumpTime", pumpTime);
        result.put("timelapseFps", timelapseFps);
        result.put("timelapseInterval", timelapseInterval);
        result.put("timelapseLength", timelapseLength);
        result.put("timelapseSwitch", timelapseSwitch);



        return result;
    }
}
