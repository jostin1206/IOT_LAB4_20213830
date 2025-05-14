package com.example.lab4_iot;

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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocationFragment} factory method to
 * create an instance of this fragment.
 */
public class LocationFragment extends Fragment {

    private EditText searchEditText;
    private Button searchButton;
    private RecyclerView recyclerView;
    private LocationAdapter adapter;
    private List<Location> locationList = new ArrayList<>();
    private final String API_KEY = "ec24b1c6dd8a4d528c1205500250305";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location, container, false);

        searchEditText = view.findViewById(R.id.searchEditText);
        searchButton = view.findViewById(R.id.searchButton);
        recyclerView = view.findViewById(R.id.locationRecyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
         /*
        adapter = new LocationAdapter(locationList);

        recyclerView.setAdapter(adapter);

        searchButton.setOnClickListener(v -> {
            String query = searchEditText.getText().toString().trim();
            if (!query.isEmpty()) {
                searchLocations(query);
            }
        }); */
        adapter = new LocationAdapter(locationList, location -> {
            Bundle bundle = new Bundle();
            bundle.putString("location_id", String.valueOf(location.id));

            bundle.putString("days", "14");

            ForecasterFragment fragment = new ForecasterFragment();
            fragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment, fragment) // esto  verifica si este ID es correcto
                    .addToBackStack(null)
                    .commit();
        });
        recyclerView.setAdapter(adapter);

        searchButton.setOnClickListener(v -> {
            String query = searchEditText.getText().toString().trim();
            if (!query.isEmpty()) {
                searchLocations(query);
            }
        });

        return view;
    }

    private void searchLocations(String locationQuery) {
        String url = "https://api.weatherapi.com/v1/search.json?key=" + API_KEY + "&q=" + locationQuery;

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    locationList.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            Location loc = new Location();
                            loc.id = obj.getInt("id");
                            loc.name = obj.getString("name");
                            loc.region = obj.getString("region");
                            loc.country = obj.getString("country");
                            loc.lat = obj.getDouble("lat");
                            loc.lon = obj.getDouble("lon");
                            locationList.add(loc);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.updateList(locationList);
                },
                error -> error.printStackTrace()
        );

        queue.add(request);
    }
}