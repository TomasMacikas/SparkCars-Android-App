package com.tomas.sparkcars.viewmodels;

import android.util.Log;

import com.tomas.sparkcars.models.Car;
import com.tomas.sparkcars.models.Filter;
import com.tomas.sparkcars.repositories.CarsRepository;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainActivityViewModel extends ViewModel {

    private MutableLiveData<List<Car>> cars;
    private CarsRepository carsRepository;

    public void init(){
        if(cars != null){
            return;
        }
        carsRepository = CarsRepository.getInstance();
        cars = carsRepository.getCars();
    }

    public LiveData<List<Car>> getCars(){
        return cars;
    }
    public LiveData<List<Car>> getCarsFromRepository(){
        cars = carsRepository.getCars();
        return cars;
    }

    public void filterCars(Filter filter){
        List<Car> carList = cars.getValue();
        carList.removeIf(p -> p.getBatteryPercentage() < filter.getBatteryPercentage());
        carList.removeIf(p -> !(p.getPlateNumber().contains(filter.getPlateNumber())));
        cars.setValue(carList);
    }
}
