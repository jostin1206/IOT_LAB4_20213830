package com.example.lab4_iot;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ForecasterAdapter extends RecyclerView.Adapter<ForecasterAdapter.ForecastViewHolder> {

    private List<Forecaster> forecastList;

    public ForecasterAdapter(List<Forecaster> forecastList) {
        this.forecastList = forecastList;
    }

    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_forecaster, parent, false);
        return new ForecastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastViewHolder holder, int position) {
        Forecaster item = forecastList.get(position);
        holder.dateText.setText("📅 Fecha: " + item.date);
        holder.tempText.setText("🌡 Máx: " + item.maxTempC + "°C - Mín: " + item.minTempC + "°C");
        holder.conditionText.setText("☁️ Clima: " + item.conditionText);
    }

    @Override
    public int getItemCount() {
        return forecastList.size();
    }

    public void updateList(List<Forecaster> newList) {
        forecastList = newList;
        notifyDataSetChanged();
    }

    static class ForecastViewHolder extends RecyclerView.ViewHolder {
        TextView dateText, tempText, conditionText;

        public ForecastViewHolder(@NonNull View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.dateText);
            tempText = itemView.findViewById(R.id.tempText);
            conditionText = itemView.findViewById(R.id.conditionText);
        }
    }
}
