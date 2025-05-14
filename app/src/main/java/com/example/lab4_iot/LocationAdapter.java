package com.example.lab4_iot;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {

    private List<Location> locationList;
    private OnLocationClickListener listener;


    public LocationAdapter(List<Location> locationList, OnLocationClickListener listener) {
        this.locationList = locationList;
        this.listener = listener;
    }


    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_location, parent, false);
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        Location loc = locationList.get(position);
        holder.nameText.setText(loc.name + " - " + loc.region);
        holder.countryText.setText(loc.country);
        holder.latLonText.setText("Lat: " + loc.lat + ", Lon: " + loc.lon);

        holder.itemView.setOnClickListener(v -> listener.onLocationClick(loc));

    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    public void updateList(List<Location> newList) {
        locationList = newList;
        notifyDataSetChanged();
    }

    static class LocationViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, countryText, latLonText;


        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.nameText);
            countryText = itemView.findViewById(R.id.countryText);
            latLonText = itemView.findViewById(R.id.latLonText);

        }
    }
}
