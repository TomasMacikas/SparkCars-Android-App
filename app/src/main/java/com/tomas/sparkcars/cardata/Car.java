package com.tomas.sparkcars.cardata;

import android.location.Location;
import android.util.Log;

public class Car {
    String id;
    String plateNumber;
    CarLocation location;
    float distanceToCar;
    Model model;
    int batteryPercentage;
    float batteryEstimatedDistance;
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
        carLoc.setLatitude(location.latitude);
        carLoc.setLongitude(location.longitude);

        float distanceInMeters = current.distanceTo(carLoc);
        Log.i("distancein", Float.toString(distanceInMeters));
        return distanceInMeters;
    }
}
