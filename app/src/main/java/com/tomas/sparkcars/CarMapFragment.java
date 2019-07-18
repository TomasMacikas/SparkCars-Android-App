package com.tomas.sparkcars;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tomas.sparkcars.cardata.Car;
import com.tomas.sparkcars.helpers.ParseJson;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

public class CarMapFragment extends Fragment implements OnMapReadyCallback {

    FragmentActivity listener;

    List<Car> concreteCars;

    GoogleMap map;

    public void setConcreteCars(List<Car> cars){
        concreteCars = cars;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        for(Car car : concreteCars){
            LatLng loc = new LatLng(car.getCarLocation().getLatitude(),
                    car.getCarLocation().getLongitude());
            MarkerOptions marker = new MarkerOptions().position(loc).
                    title(car.getPlateNumber()).
                    snippet(car.getBatteryEstimatedDistance() + " km").
                    icon(BitmapDescriptorFactory.fromResource(R.drawable.car_logo));

            googleMap.addMarker(marker);
        }
        LatLng loc = new LatLng(54.67, 25.270);
        float zoomLevel = 11.0f;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, zoomLevel));
    }

    public void updateMap(List<Car> cars){
        map.clear();
        concreteCars = cars;
        sortCars();
        for(Car car : concreteCars){
            LatLng loc = new LatLng(car.getCarLocation().getLatitude(),
                    car.getCarLocation().getLongitude());
            MarkerOptions marker = new MarkerOptions().position(loc).
                    title(car.getPlateNumber()).
                    snippet(car.getBatteryEstimatedDistance() + " km").
                    icon(BitmapDescriptorFactory.fromResource(R.drawable.car_logo));

            map.addMarker(marker);
        }
    }

    public void sortCars(){
        Comparator<Car> cmp = (car1, car2) -> Float.compare(car1.getDistanceToCar(), car2.getDistanceToCar());
        concreteCars.sort(cmp);
    }

    public CarMapFragment() {
    }

    public static CarMapFragment newInstance(String cars) {
        CarMapFragment fragment = new CarMapFragment();
        Bundle args = new Bundle();
        args.putString("cars", cars);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String cars = "";
        if(getArguments()!=null){
            cars = getArguments().getString("cars", "");
            concreteCars = ParseJson.fromJson(cars);
            Log.i("printcars", cars);
        }
        else{
            concreteCars = new ArrayList<>();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.listener = (FragmentActivity) context;
        } else {
            throw new RuntimeException(context.toString());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


}
