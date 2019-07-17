package com.tomas.sparkcars;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import com.tomas.sparkcars.cardata.Car;
import com.tomas.sparkcars.helpers.DownloadImageTask;
import com.tomas.sparkcars.helpers.ParseJson;

import java.util.List;
import java.util.concurrent.ExecutionException;

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
                    ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
                    progressBar.setVisibility(View.VISIBLE);
                    //Loading here
                    cars = ParseJson.fromJson(new ParseJson().execute("https://development.espark.lt/api/mobile/public/availablecars").get());
                    for (Car car : cars){
                        Bitmap img = null;
                        try {
                            img = new DownloadImageTask().execute(car.getModel().getPhotoUrl()).get();
                            car.getModel().setPhoto(img);
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {

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
