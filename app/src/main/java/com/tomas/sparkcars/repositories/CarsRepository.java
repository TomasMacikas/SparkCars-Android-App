package com.tomas.sparkcars.repositories;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.tomas.sparkcars.MainActivity;
import com.tomas.sparkcars.R;
import com.tomas.sparkcars.SplashActivity;
import com.tomas.sparkcars.helpers.ParseJson;
import com.tomas.sparkcars.models.Car;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import androidx.lifecycle.MutableLiveData;

public class CarsRepository {
    private static CarsRepository instance;

    private ArrayList<Car> dataset = new ArrayList<>();

    public static CarsRepository getInstance(){
        if(instance == null){
            instance = new CarsRepository();
        }
        return instance;
    }
    public MutableLiveData<List<Car>> getCars(){
        MutableLiveData<List<Car>> data = new MutableLiveData<>();
        try {
            dataset = (ArrayList<Car>) ParseJson.fromJson(new ParseJson().
                    execute("https://development.espark.lt/api/mobile/public/availablecars").get());
            Log.i("CarDataset", dataset.toString());
            data.setValue(dataset);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return data;
    }
}
