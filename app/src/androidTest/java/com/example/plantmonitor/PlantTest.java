package com.example.plantmonitor;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class PlantTest {
    @Test
    public void nonNullplantName() {
        Plant plant = new Plant("Lettuce","December","February",15,30,1.0,5.0,15.0,30.0,50,8,5.0,12.0,10.0,10.0,25.0,24.0,80,null,null);
        Assert.assertEquals("Lettuce", plant.getPlantName());

    }


}