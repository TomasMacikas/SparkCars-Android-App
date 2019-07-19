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
import com.tomas.sparkcars.models.Car;
import com.tomas.sparkcars.helpers.MainPresenter;
import com.tomas.sparkcars.helpers.MainView;
import com.tomas.sparkcars.adapters.ScreenSlidePagerAdapter;
import com.tomas.sparkcars.models.Filter;
import com.tomas.sparkcars.viewmodels.MainActivityViewModel;

import java.util.List;
import java.util.concurrent.TimeUnit;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity implements MainView {

    private TextView lastUpdate;
    private TextView locationText;

    private MainPresenter presenter;

    private ViewPager mPager;
    private PagerAdapter pagerAdapter;

    CarListFragment carListFragment;
    CarMapFragment carMapFragment;


    private MainActivityViewModel mainActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        mainActivityViewModel.init();

        carMapFragment = new CarMapFragment();
        carListFragment = new CarListFragment();

        mainActivityViewModel.getCars().observe(this, new Observer<List<Car>>() {
            @Override
            public void onChanged(List<Car> cars) {
                //carListFragment.updateList();
                //carMapFragment.updateMap();
            }
        });
        //Creating and setting up Pager
        mPager = findViewById(R.id.pager);
        initPager();

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

    public void initPager(){
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        ((ScreenSlidePagerAdapter) pagerAdapter).setCarListFragment(carListFragment);
        ((ScreenSlidePagerAdapter) pagerAdapter).setCarMapFragment(carMapFragment);
        mPager.setAdapter(pagerAdapter);
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

        for(Car car : mainActivityViewModel.getCars().getValue()){
            car.setDistanceToCar(car.calculateDistance(location));
        }
        carListFragment.updateList();
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

        //Update Cars from API
        mainActivityViewModel.getCarsFromRepository();

        Filter filter = new Filter();
        if(resultCode == 1){
            int batteryPercentage =data.getIntExtra("battery", 50);
            filter.setBatteryPercentage(batteryPercentage);
            filter.setPlateNumber("");
            mainActivityViewModel.filterCars(filter);
        }
        else if(resultCode == 2){
            int batteryPercentage =data.getIntExtra("battery", 50);
            String plate = data.getStringExtra("plate");
            filter.setBatteryPercentage(batteryPercentage);
            filter.setPlateNumber(plate);
            mainActivityViewModel.filterCars(filter);
        }
        else {

        }
        carListFragment.updateList();
        carMapFragment.updateMap();
    }
}

