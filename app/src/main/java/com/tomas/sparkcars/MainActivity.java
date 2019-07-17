package com.tomas.sparkcars;

import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.patloew.rxlocation.RxLocation;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tomas.sparkcars.cardata.Car;
import com.tomas.sparkcars.helpers.CarAdapter;
import com.tomas.sparkcars.helpers.MainPresenter;
import com.tomas.sparkcars.helpers.MainView;
import com.tomas.sparkcars.helpers.ParseJson;

import java.text.DateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements MainView {

    private static final DateFormat DATE_FORMAT = DateFormat.getDateTimeInstance();
    private TextView lastUpdate;
    private TextView locationText;
    private TextView addressText;

    private RxLocation rxLocation;

    private MainPresenter presenter;

    private RecyclerView carsRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    List<Car> cars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent i = getIntent();
        String carsStr = i.getStringExtra("cars");

        cars = ParseJson.fromJson(carsStr);
        //Location calculations
        lastUpdate = findViewById(R.id.tv_last_update);
        locationText = findViewById(R.id.tv_current_location);

        //Permissions
        final RxPermissions rxPermissions = new RxPermissions(this);

        rxLocation = new RxLocation(this);
        rxLocation.setDefaultTimeout(15, TimeUnit.SECONDS);

        presenter = new MainPresenter(rxLocation);

//        rxPermissions
//                .request(Manifest.permission.ACCESS_FINE_LOCATION)
//                .subscribe(granted -> {
//                    if (granted) { // Always true pre-M
//                    } else {
//                        // Oups permission denied
//                    }
//                });


        //RECYCLER VIEW
        carsRecyclerView = (RecyclerView) findViewById(R.id.carsRecyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        carsRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        carsRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),
                DividerItemDecoration.VERTICAL));
        carsRecyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        mAdapter = new CarAdapter(cars);
        carsRecyclerView.setAdapter(mAdapter);
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

        for(Car car : cars){
            car.setDistanceToCar(car.calculateDistance(location));
        }
        //Sorting

        Comparator<Car> cmp = new Comparator<Car>() {
            public int compare(Car car1, Car car2) {
                return Float.valueOf(car1.getDistanceToCar()).
                        compareTo(Float.valueOf(car2.getDistanceToCar()));
            }
        };
        cars.sort(cmp);

        mAdapter = new CarAdapter(cars);
        carsRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onAddressUpdate(Address address) {
        return;
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
                startActivity(intent);

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

