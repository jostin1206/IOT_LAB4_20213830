package com.example.lab4_iot;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FutureFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FutureFragment extends Fragment {

    private EditText idEditText, dateEditText;
    private Button searchButton;
    private RecyclerView recyclerView;
    private FutureAdapter adapter;
    private TextView locationInfoText;

    private List<Future> futureList = new ArrayList<>();
    private final String API_KEY = "ec24b1c6dd8a4d528c1205500250305";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_future, container, false);

        idEditText = view.findViewById(R.id.futureIdEditText);
        dateEditText = view.findViewById(R.id.futureDateEditText);
        searchButton = view.findViewById(R.id.searchFutureButton);
        recyclerView = view.findViewById(R.id.futureRecyclerView);

        locationInfoText = view.findViewById(R.id.locationInfoText);


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FutureAdapter(futureList);
        recyclerView.setAdapter(adapter);

        searchButton.setOnClickListener(v -> {
            String id = idEditText.getText().toString().trim();
            String date = dateEditText.getText().toString().trim();

            if (!id.isEmpty() && !date.isEmpty()) {
                fetchFutureForecast(id, date);
            }
        });

        return view;
    }

    private void fetchFutureForecast(String id, String date) {
        String url = "https://api.weatherapi.com/v1/future.json?key=" + API_KEY + "&q=id:" + id + "&dt=" + date;

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    futureList.clear();
                    try {

                        JSONObject locationObj = response.getJSONObject("location");
                        String name = locationObj.getString("name");
                        String region = locationObj.getString("region");
                        String country = locationObj.getString("country");

                        String locationInfo = name + " - " + region + ", " + country;
                        locationInfoText.setText("Lugar: " + locationInfo);


                        JSONObject forecast = response.getJSONObject("forecast");
                        JSONArray hours = forecast
                                .getJSONArray("forecastday")
                                .getJSONObject(0)
                                .getJSONArray("hour");

                        for (int i = 0; i < hours.length(); i++) {
                            JSONObject hourObj = hours.getJSONObject(i);
                            JSONObject condition = hourObj.getJSONObject("condition");

                            Future item = new Future();
                            item.time = hourObj.getString("time");
                            item.tempC = hourObj.getDouble("temp_c");
                            item.windKph = hourObj.getDouble("wind_kph");
                            item.conditionText = condition.getString("text");
                            item.iconUrl = condition.getString("icon");
                            item.isDay = hourObj.getInt("is_day");

                            futureList.add(item);
                        }

                        adapter.updateList(futureList);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
        );

        queue.add(request);
    }
}