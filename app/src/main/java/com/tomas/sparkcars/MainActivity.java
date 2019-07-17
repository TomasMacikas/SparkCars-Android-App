package com.tomas.sparkcars;

import android.Manifest;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.snackbar.Snackbar;
import com.patloew.rxlocation.RxLocation;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tomas.sparkcars.cardata.Car;
import com.tomas.sparkcars.helpers.CarAdapter;
import com.tomas.sparkcars.helpers.MainPresenter;
import com.tomas.sparkcars.helpers.MainView;
import com.tomas.sparkcars.helpers.ParseJson;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity implements MainView {

    private static final DateFormat DATE_FORMAT = DateFormat.getDateTimeInstance();
    private TextView lastUpdate;
    private TextView locationText;

    private MainPresenter presenter;

    private RecyclerView carsRecyclerView;
    private RecyclerView.Adapter mAdapter;

    List<Car> cars;
    List<Car> concreteCars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent i = getIntent();
        String carsStr = i.getStringExtra("cars");

        cars = ParseJson.fromJson(carsStr);

        concreteCars = new ArrayList<>(cars);
        //Location calculations
        lastUpdate = findViewById(R.id.tv_last_update);
        locationText = findViewById(R.id.tv_current_location);

        //Permissions

        RxLocation rxLocation = new RxLocation(this);
        rxLocation.setDefaultTimeout(15, TimeUnit.SECONDS);

        presenter = new MainPresenter(rxLocation);
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(granted -> {
                    if (granted) { // Always true pre-M
                    } else {
                        // Oups permission denied
                    }
                });

        // Within the activity
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        CarListFragment fragmentDemo = CarListFragment.newInstance(carsStr);
        ft.replace(R.id.fragment, fragmentDemo);
        ft.commit();

        //Map fragment


//        //RECYCLER VIEW
//        carsRecyclerView = findViewById(R.id.carsRecyclerView);
//
//        // use this setting to improve performance if you know that changes
//        // in content do not change the layout size of the RecyclerView
//        carsRecyclerView.setHasFixedSize(true);
//
//        // use a linear layout manager
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
//        carsRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),
//                DividerItemDecoration.VERTICAL));
//         carsRecyclerView.setLayoutManager(layoutManager);
//
//        // specify an adapter (see also next example)
//        mAdapter = new CarAdapter(concreteCars);
//        carsRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.attachView(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServicesAvailable();
    }

    public void onLocationUpdate(Location location) {
        Log.i("location", location.getLatitude() + ", " + location.getLongitude());
        lastUpdate.setText(DATE_FORMAT.format(new Date()));
        locationText.setText(location.getLatitude() + ", " + location.getLongitude());

        for(Car car : concreteCars){
            car.setDistanceToCar(car.calculateDistance(location));
        }

        CarListFragment frag = (CarListFragment) getSupportFragmentManager().
                findFragmentById(R.id.fragment);

        frag.updateList(concreteCars);

    }

    @Override
    public void onAddressUpdate(Address address) {
    }

    @Override
    public void onLocationSettingsUnsuccessful() {
        Snackbar.make(lastUpdate, "Location settings requirements not satisfied. Showing last known location if available.", Snackbar.LENGTH_INDEFINITE)
                .setAction("Retry", view -> presenter.startLocationRefresh())
                .show();
    }

    private void checkPlayServicesAvailable() {
        final GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int status = apiAvailability.isGooglePlayServicesAvailable(this);

        if(status != ConnectionResult.SUCCESS) {
            if(apiAvailability.isUserResolvableError(status)) {
                apiAvailability.getErrorDialog(this, status, 1).show();
            } else {
                Snackbar.make(lastUpdate, "Google Play Services unavailable. This app will not work", Snackbar.LENGTH_INDEFINITE).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.activity_main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter:
                Intent intent = new Intent(getApplicationContext(), FilterActivity.class);
                startActivityForResult(intent, 0);
                break;
            case R.id.map:
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                CarMapFragment frag = CarMapFragment.newInstance(ParseJson.toJson(concreteCars));
                ft.replace(R.id.fragment, frag);
                ft.commit();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //concreteCars.clear();
        concreteCars = new ArrayList<>(cars);

        if(resultCode == 1){
            int batteryPercentage =data.getIntExtra("battery", 50);
            Log.i("Battery Limit", Integer.toString(batteryPercentage));
            concreteCars.removeIf(p -> p.getBatteryPercentage() < batteryPercentage);
        }
        else if(resultCode == 2){
            int batteryPercentage =data.getIntExtra("battery", 50);
            Log.i("Battery Limit", Integer.toString(data.getIntExtra("battery", 50)));
            String plate = data.getStringExtra("plate");
            concreteCars.removeIf(p -> p.getBatteryPercentage() < batteryPercentage);
            concreteCars.removeIf(p -> !(p.getPlateNumber().contains(plate)));
        }
        else {
            //UNFILTER
        }

        CarListFragment frag = (CarListFragment) getSupportFragmentManager().
                findFragmentById(R.id.fragment);

        frag.updateList(concreteCars);
    }
}

