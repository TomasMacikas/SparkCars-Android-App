package com.tomas.sparkcars.helpers;

import com.tomas.sparkcars.CarListFragment;
import com.tomas.sparkcars.CarMapFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

    CarMapFragment carMapFragment;
    CarListFragment carListFragment;

    public void setCarMapFragment(CarMapFragment carMapFragment) {
        this.carMapFragment = carMapFragment;
    }

    public void setCarListFragment(CarListFragment carListFragment) {
        this.carListFragment = carListFragment;
    }

    public ScreenSlidePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0){
            return carMapFragment;
        } else{
            return carListFragment;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}