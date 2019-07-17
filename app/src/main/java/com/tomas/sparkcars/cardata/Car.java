package com.tomas.sparkcars.cardata;

import android.location.Location;
import android.util.Log;

public class Car {
    private String id;
    private String plateNumber;
    private CarLocation location;
    private float distanceToCar;
    private Model model;
    private int batteryPercentage;
    private float batteryEstimatedDistance;
    //boolean isCharging;

    //etc


    public String getPlateNumber() {
        return plateNumber;
    }

    public CarLocation getCarLocation() {
        return location;
    }

    public Model getModel() {
        return model;
    }

    public int getBatteryPercentage() {
        return batteryPercentage;
    }

    public float getBatteryEstimatedDistance() {
        return batteryEstimatedDistance;
    }

    public float getDistanceToCar() {
        return distanceToCar;
    }

    public void setDistanceToCar(float distanceToCar) {
        this.distanceToCar = distanceToCar;
    }
    public float calculateDistance(Location current){
        Location carLoc = new Location("");
        carLoc.setLatitude(location.getLatitude());
        carLoc.setLongitude(location.getLongitude());

        float distanceInMeters = current.distanceTo(carLoc);
        Log.i("distancein", Float.toString(distanceInMeters));
        return distanceInMeters;
    }
}
