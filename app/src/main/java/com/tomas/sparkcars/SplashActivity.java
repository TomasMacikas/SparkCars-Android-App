package com.tomas.sparkcars;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.tomas.sparkcars.models.Car;
import com.tomas.sparkcars.helpers.ParseJson;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        Thread welcomeThread = new Thread() {
            @Override
            public void run() {
                List<Car> cars = null;
                try {
                    super.run();
                    ProgressBar progressBar = findViewById(R.id.progressBar);
                    progressBar.setVisibility(View.VISIBLE);
                    //Loading here
                    cars = ParseJson.fromJson(new ParseJson().execute("https://development.espark.lt/api/mobile/public/availablecars").get());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                    Intent i = new Intent(SplashActivity.this,
                            MainActivity.class);

                    i.putExtra("cars", ParseJson.toJson(cars));
                    startActivity(i);
                    finish();
                }
            }
        };
        welcomeThread.start();
    }
}
