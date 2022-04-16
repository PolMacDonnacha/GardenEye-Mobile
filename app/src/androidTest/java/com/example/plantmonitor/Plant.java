package com.example.plantmonitor;

import java.util.Calendar;
import java.util.Date;

public class Plant {
    String plantName;
    String plantType;
    int daysUntilHarvest;
    Date datePlanted;
   // Date harvestDate;


    public String getPlantName() {
        return plantName;
    }
    public Plant(String name,String type,Date _datePlanted){
        plantName = name;
        plantType = type;
        //daysUntilHarvest = _daysUntilHarvest;
        datePlanted = _datePlanted;
    }

    /*public int setHarvestDate() {
        Calendar cal = Calendar.getInstance();
        harvestDate = datePlanted. daysUntilHarvest;
    }*/
}
