package com.tomas.sparkcars.helpers;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tomas.sparkcars.R;
import com.tomas.sparkcars.cardata.Car;

import java.util.List;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.MyViewHolder> {
    private List<Car> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView title;
        private TextView plateNumber;
        private TextView distanceToCar;
        private TextView estimatedDistance;
        private ImageView carImageView;
        private TextView address;
        public MyViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.title);
            plateNumber = (TextView) v.findViewById(R.id.plateNumber);
            distanceToCar = (TextView) v.findViewById(R.id.distanceToCar);
            estimatedDistance = (TextView) v.findViewById(R.id.estimatedDistance);
            carImageView = (ImageView) v.findViewById(R.id.carImageView);
            address = (TextView) v.findViewById(R.id.address);
         }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public CarAdapter(List<Car> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CarAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row, parent, false);
        //MyViewHolder vh = new MyViewHolder(v);
        return new MyViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Car car = mDataset.get(position);

        holder.title.setText(car.getModel().getTitle());
        holder.plateNumber.setText(car.getPlateNumber());
        holder.distanceToCar.setText(Float.toString(car.getDistanceToCar()));
        holder.estimatedDistance.setText("Distance left: " + Float.toString(car.getBatteryEstimatedDistance()) + "km");
        holder.address.setText(car.getCarLocation().getAddress());

        holder.carImageView.setImageBitmap(car.getModel().getPhoto());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}