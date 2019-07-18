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
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.snackbar.Snackbar;
import com.patloew.rxlocation.RxLocation;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tomas.sparkcars.cardata.Car;
import com.tomas.sparkcars.helpers.MainPresenter;
import com.tomas.sparkcars.helpers.MainView;
import com.tomas.sparkcars.helpers.ParseJson;
import com.tomas.sparkcars.helpers.ScreenSlidePagerAdapter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity implements MainView {

    private TextView lastUpdate;
    private TextView locationText;

    private MainPresenter presenter;

    List<Car> cars;
    List<Car> concreteCars; //Cars after filtering, for fragments

    private ViewPager mPager;
    private PagerAdapter pagerAdapter;

    CarListFragment carListFragment;
    CarMapFragment carMapFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Getting cars from json
        Intent i = getIntent();
        String carsStr = i.getStringExtra("cars");
        cars = ParseJson.fromJson(carsStr);

        concreteCars = new ArrayList<>(cars);

        //Creating Fragments
        carMapFragment = CarMapFragment.newInstance(carsStr);
        carListFragment = CarListFragment.newInstance(carsStr);

        //Creating and setting up Pager
        mPager = findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        ((ScreenSlidePagerAdapter) pagerAdapter).setCarListFragment(carListFragment);
        ((ScreenSlidePagerAdapter) pagerAdapter).setCarMapFragment(carMapFragment);
        mPager.setAdapter(pagerAdapter);
        //Permissions
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(granted -> {
                    if (granted) {
                        RxLocation rxLocation = new RxLocation(this);
                        rxLocation.setDefaultTimeout(15, TimeUnit.SECONDS);
                        presenter = new MainPresenter(rxLocation);
                    } else {
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
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

        for(Car car : concreteCars){
            car.setDistanceToCar(car.calculateDistance(location));
        }

        carListFragment.updateList(concreteCars);

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
            case R.id.changePerspective:
                if(mPager.getCurrentItem() == 0){
                    mPager.setCurrentItem(1);
                }else{
                    mPager.setCurrentItem(0);
                }
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
        carListFragment.updateList(concreteCars);
        carMapFragment.updateMap(concreteCars);
    }
}

