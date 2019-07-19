package com.tomas.sparkcars.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tomas.sparkcars.R;
import com.tomas.sparkcars.models.Car;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.MyViewHolder> {
    private List<Car> mDataset;
    private Context context;

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
            title = v.findViewById(R.id.title);
            plateNumber = v.findViewById(R.id.plateNumber);
            distanceToCar = v.findViewById(R.id.distanceToCar);
            estimatedDistance = v.findViewById(R.id.estimatedDistance);
            carImageView = v.findViewById(R.id.carImageView);
            address = v.findViewById(R.id.address);
         }

        public ImageView getCarImageView() {
            return carImageView;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public CarAdapter(List<Car> myDataset, Context context) {
        this.context = context;
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
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
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Car car = mDataset.get(position);

        holder.title.setText(car.getModel().getTitle());
        holder.plateNumber.setText(car.getPlateNumber());
        holder.distanceToCar.setText(String.format("%.1f",car.getDistanceToCar()/1000)+ " km away");
        holder.estimatedDistance.setText("Estimated distance: " + String.format("%.0f",car.getBatteryEstimatedDistance()) + " km");
        holder.address.setText(car.getCarLocation().getAddress());

        Glide.with(this.context)
                .load(car.getModel().getPhotoUrl()).
                diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.getCarImageView());

        //holder.carImageView.setImageBitmap(car.getModel().getPhoto());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}