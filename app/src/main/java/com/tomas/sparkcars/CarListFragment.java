package com.tomas.sparkcars;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tomas.sparkcars.models.Car;
import com.tomas.sparkcars.adapters.CarAdapter;
import com.tomas.sparkcars.viewmodels.MainActivityViewModel;

import java.util.Comparator;

public class CarListFragment extends Fragment {

    FragmentActivity listener;

    private RecyclerView carsRecyclerView;
    private RecyclerView.Adapter mAdapter;

    MainActivityViewModel mainActivityViewModel;


    public void updateList(){
        if(getActivity() != null){
            mainActivityViewModel = ViewModelProviders.of(getActivity()).get(MainActivityViewModel.class);
            sortCars();
            mAdapter = new CarAdapter(mainActivityViewModel.getCars().getValue(), getContext());
            carsRecyclerView.setAdapter(mAdapter);
        }
    }

    public void sortCars(){
        Comparator<Car> cmp = (car1, car2) -> Float.compare(car1.getDistanceToCar(), car2.getDistanceToCar());
        mainActivityViewModel.getCars().getValue().sort(cmp);
    }

    public CarListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivityViewModel = ViewModelProviders.of(getActivity()).get(MainActivityViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_carlist, container, false);

//        RECYCLER VIEW
        carsRecyclerView = view.findViewById(R.id.carsRecyclerView);

        carsRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        carsRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
        carsRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new CarAdapter(mainActivityViewModel.getCars().getValue(), getContext());
        carsRecyclerView.setAdapter(mAdapter);

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
