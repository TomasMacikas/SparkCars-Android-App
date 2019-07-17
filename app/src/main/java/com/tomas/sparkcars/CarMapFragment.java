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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tomas.sparkcars.cardata.Car;
import com.tomas.sparkcars.helpers.CarAdapter;
import com.tomas.sparkcars.helpers.ParseJson;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CarMapFragment extends Fragment implements OnMapReadyCallback {

    FragmentActivity listener;

    List<Car> concreteCars;

    private RecyclerView carsRecyclerView;
    private RecyclerView.Adapter mAdapter;


    public void setConcreteCars(List<Car> cars){
        concreteCars = cars;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        for(Car car : concreteCars){
            LatLng loc = new LatLng(car.getCarLocation().getLatitude(),
                    car.getCarLocation().getLongitude());
            googleMap.addMarker(new MarkerOptions().position(loc)
                    .title(car.getPlateNumber()));
        }
        LatLng loc = new LatLng(54.67, 25.270);
        float zoomLevel = 10.0f;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, zoomLevel));
    }

    public void updateList(List<Car> cars){
        concreteCars = cars;
        sortCars();
        mAdapter = new CarAdapter(concreteCars);
        carsRecyclerView.setAdapter(mAdapter);
    }

    public void sortCars(){
        Comparator<Car> cmp = (car1, car2) -> Float.compare(car1.getDistanceToCar(), car2.getDistanceToCar());
        concreteCars.sort(cmp);
    }

    public CarMapFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
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
        View view = inflater.inflate(R.layout.activity_maps, container, false);

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
