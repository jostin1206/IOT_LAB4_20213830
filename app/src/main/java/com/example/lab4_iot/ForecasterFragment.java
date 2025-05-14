package com.example.lab4_iot;

import android.app.AlertDialog;
import android.content.Context;
import android.hardware.SensorEventListener;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ForecasterFragment#} factory method to
 * create an instance of this fragment.
 */
public class ForecasterFragment extends Fragment implements SensorEventListener {

    private EditText idEditText, daysEditText;
    private Button searchButton;
    private RecyclerView recyclerView;
    private ForecasterAdapter adapter;
    private List<Forecaster> forecastList = new ArrayList<>();
    private final String API_KEY = "ec24b1c6dd8a4d528c1205500250305";

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long lastShakeTime = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forecaster, container, false);

        idEditText = view.findViewById(R.id.idEditText);
        daysEditText = view.findViewById(R.id.daysEditText);
        searchButton = view.findViewById(R.id.searchForecastButton);
        recyclerView = view.findViewById(R.id.forecastRecyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ForecasterAdapter(forecastList);
        recyclerView.setAdapter(adapter);

        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


        Bundle args = getArguments();
        if (args != null && args.containsKey("location_id")) {
            String id = args.getString("location_id");
            String days = args.getString("days", "14");

            idEditText.setText(id);
            daysEditText.setText(days);

            fetchForecast(id, days);
        }
        searchButton.setOnClickListener(v -> {
            String id = idEditText.getText().toString().trim();
            String days = daysEditText.getText().toString().trim();

            if (!id.isEmpty() && !days.isEmpty()) {
                fetchForecast(id, days);
            }
        });

        return view;
    }

    private void fetchForecast(String id, String days) {
        String url = "https://api.weatherapi.com/v1/forecast.json?key=" + API_KEY + "&q=id:" + id + "&days=" + days;

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    forecastList.clear();
                    try {
                        JSONObject forecastObj = response.getJSONObject("forecast");
                        JSONArray forecastDays = forecastObj.getJSONArray("forecastday");

                        for (int i = 0; i < forecastDays.length(); i++) {
                            JSONObject dayObj = forecastDays.getJSONObject(i);
                            JSONObject dayData = dayObj.getJSONObject("day");
                            JSONObject condition = dayData.getJSONObject("condition");

                            Forecaster item = new Forecaster();
                            item.date = dayObj.getString("date");
                            item.maxTempC = dayData.getDouble("maxtemp_c");
                            item.minTempC = dayData.getDouble("mintemp_c");
                            item.conditionText = condition.getString("text");

                            forecastList.add(item);
                        }

                        adapter.updateList(forecastList);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
        );

        queue.add(request);
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        // Calculamos la magnitud de la aceleración
        double acceleration = Math.sqrt(x * x + y * y + z * z);

        if (acceleration > 20) {
            long currentTime = System.currentTimeMillis();

            // Esto e vita que se dispare múltiples veces en poco tiempo
            if (currentTime - lastShakeTime > 1000) {
                lastShakeTime = currentTime;
                showShakeDialog();  // Mostrarmos el diálogo
            }
        }
    }
    private void showShakeDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("¿Eliminar pronósticos?")
                .setMessage("Se detectó una agitación fuerte. ¿Deseas borrar la lista?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    forecastList.clear();
                    adapter.updateList(forecastList);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

}