package com.example.lab4_iot;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
public class FutureAdapter extends RecyclerView.Adapter<FutureAdapter.FutureViewHolder> {

    private List<Future> futureList;

    public FutureAdapter(List<Future> futureList) {
        this.futureList = futureList;
    }

    @NonNull
    @Override
    public FutureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_future, parent, false);
        return new FutureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FutureViewHolder holder, int position) {
        Future item = futureList.get(position);
        holder.timeText.setText("Hora " + item.time);
        holder.tempText.setText("Temp: " + item.tempC + "°C");
        holder.conditionText.setText("Condición: " + item.conditionText);
        holder.windText.setText("Viento: " + item.windKph + " kph");

        // Cargar ícono del clima usando Glide
        Glide.with(holder.itemView.getContext())
                .load("https:" + item.iconUrl)
                .into(holder.iconImage);
    }

    @Override
    public int getItemCount() {
        return futureList.size();
    }

    public void updateList(List<Future> newList) {
        futureList = newList;
        notifyDataSetChanged();
    }

    static class FutureViewHolder extends RecyclerView.ViewHolder {
        TextView timeText, tempText, conditionText, windText;
        ImageView iconImage;

        public FutureViewHolder(@NonNull View itemView) {
            super(itemView);
            timeText = itemView.findViewById(R.id.timeText);
            tempText = itemView.findViewById(R.id.tempText);
            conditionText = itemView.findViewById(R.id.conditionText);
            windText = itemView.findViewById(R.id.windText);
            iconImage = itemView.findViewById(R.id.iconImage);
        }
    }
}