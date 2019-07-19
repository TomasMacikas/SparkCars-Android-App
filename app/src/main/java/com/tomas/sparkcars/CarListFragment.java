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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tomas.sparkcars.models.Car;
import com.tomas.sparkcars.adapters.CarAdapter;
import com.tomas.sparkcars.helpers.ParseJson;
import com.tomas.sparkcars.viewmodels.MainActivityViewModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static CarListFragment newInstance(String cars) {
        CarListFragment fragment = new CarListFragment();
        Bundle args = new Bundle();
        args.putString("cars", cars);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivityViewModel = ViewModelProviders.of(getActivity()).get(MainActivityViewModel.class);

//        String cars = "";
//        if(getArguments()!=null){
//            cars = getArguments().getString("cars", "");
//            concreteCars = ParseJson.fromJson(cars);
//            Log.i("printcars", cars);
//        }
//        else{
//            concreteCars = new ArrayList<>();
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_carlist, container, false);

//        RECYCLER VIEW
        carsRecyclerView = view.findViewById(R.id.carsRecyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        carsRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        carsRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
         carsRecyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
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
