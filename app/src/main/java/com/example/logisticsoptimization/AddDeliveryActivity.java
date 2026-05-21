package com.example.logisticsoptimization;

import android.net.Uri;
import com.google.android.gms.maps.model.LatLng;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.WindowDecorActionBar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddDeliveryActivity extends BaseActivity {
    private DriverAdapter driverAdapter;
    private VehicleAdapter vehicleAdapter;
    private List<Driver> driverList = new ArrayList<>();
    private List<Vehicle> vehicleList = new ArrayList<>();
    private Driver selectedDriver = null;
    private Vehicle selectedVehicle = null;
    private TextInputEditText etDeliveryAddress;
    private TextInputEditText etPickupAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_delivery);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etDeliveryAddress = findViewById(R.id.etDeliveryAddress);
        etPickupAddress = findViewById(R.id.etPickupAddress);
        TextInputEditText etDeliveryWeight = findViewById(R.id.etDeliveryWeight);
        RecyclerView rvDrivers = findViewById(R.id.rvDrivers);
        RecyclerView rvVehicles = findViewById(R.id.rvVehicles);
        MaterialButton btnSubmit = findViewById(R.id.btnSubmitDelivery);


        driverAdapter = new DriverAdapter(this, driverList, driver -> {
            selectedDriver = driver;
        });
        vehicleAdapter = new VehicleAdapter(this, vehicleList, vehicle -> {
            selectedVehicle = vehicle;
        });

        rvDrivers.setLayoutManager(new LinearLayoutManager(this));
        rvDrivers.setAdapter(driverAdapter);
        rvDrivers.setNestedScrollingEnabled(false);

        rvVehicles.setLayoutManager(new LinearLayoutManager(this));
        rvVehicles.setAdapter(vehicleAdapter);
        rvVehicles.setNestedScrollingEnabled(false);
        loadDrivers();
        loadVehicles();

        btnSubmit.setOnClickListener(v -> {
            String address = etDeliveryAddress.getText().toString().trim();
            String weight = etDeliveryWeight.getText().toString().trim();
            String pickupAddress = etPickupAddress.getText().toString().trim();

            if (address.isEmpty() || weight.isEmpty()) {
                Toast.makeText(this, "Completează adresa și greutatea!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedDriver == null) {
                Toast.makeText(this, "Selectează un șofer!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedVehicle == null) {
                Toast.makeText(this, "Selectează un vehicul!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (pickupAddress.isEmpty() || address.isEmpty() || weight.isEmpty()) {
                Toast.makeText(this, "Completează toate câmpurile!", Toast.LENGTH_SHORT).show();
                return;
            }


//            addDelivery(address, weight, selectedDriver.getId(), selectedVehicle.getId());
            calculateRouteAndCost(pickupAddress,address,selectedVehicle);


        });
    }

    private void loadDrivers() {
        String url = "http://192.168.1.245:3000/api/drivers";

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    Log.d("ADD_DELIVERY", "Drivers response: " + response.toString());

                    try {
                        driverList.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            driverList.add(new Driver(
                                    obj.getInt("id"),
                                    obj.getString("name"),
                                    obj.getString("username")
                            ));
                        }
                        driverAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Eroare la incarcarea soferilor!", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void loadVehicles() {
        String url = "http://192.168.1.245:3000/api/vehicles";

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    Log.d("ADD_DELIVERY", "Vehicles response: " + response.toString());

                    try {
                        vehicleList.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            vehicleList.add(new Vehicle(
                                    obj.getInt("id"),
                                    obj.getString("model"),
                                    obj.getString("plate_number"),
                                    obj.getDouble("fuel_consumption"),
                                    obj.getString("fuel_type")
                            ));
                        }
                        vehicleAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Eroare la incarcarea vehiculelor!", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }
    private void calculateRouteAndCost(String pickupAddress, String deliveryAddress, Vehicle vehicle) {
        Log.d("CALC", "calculateRouteAndCost called with pickup: " + pickupAddress + ", delivery: " + deliveryAddress);

        // Geocodează adresele
        geocodeAddress(pickupAddress, pickupLatLng -> {
            Log.d("CALC", "Pickup geocoded: " + pickupLatLng);
            geocodeAddress(deliveryAddress, deliveryLatLng -> {
                Log.d("CALC", "Delivery geocoded: " + deliveryLatLng);

                // Apelează Directions API
                getRouteDetails(pickupLatLng, deliveryLatLng, vehicle);
            });
        });
    }

    private void getRouteDetails(LatLng origin, LatLng destination, Vehicle vehicle) {
        String url = "https://maps.googleapis.com/maps/api/directions/json?"
                + "origin=" + origin.latitude + "," + origin.longitude
                + "&destination=" + destination.latitude + "," + destination.longitude
                + "&key=AIzaSyAFs2HB2OdG2X7-HbZNjkQ3HXbFrGm7IAE";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        JSONArray routes = response.getJSONArray("routes");
                        if (routes.length() > 0) {
                            JSONObject leg = routes.getJSONObject(0).getJSONArray("legs").getJSONObject(0);

                            int distanceMeters = leg.getJSONObject("distance").getInt("value");
                            int durationSeconds = leg.getJSONObject("duration").getInt("value");

                            double distanceKm = distanceMeters / 1000.0;
                            int estimatedTimeMinutes = durationSeconds / 60;
                            String estimatedTime = formatDuration(durationSeconds);

                            calculateCost(distanceKm, vehicle, estimatedTimeMinutes);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Eroare la calculul rutei!", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void calculateCost(double distanceKm, Vehicle vehicle, int estimatedTimeMinutes) {
        String url = "http://192.168.1.245:3000/api/fuel-price/" + vehicle.getFuelType();

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        double fuelPrice = response.getDouble("price");

                        // Formula: (distanță × consum × preț) / 100
                        double estimatedCost = (distanceKm * vehicle.getFuelConsumption() * fuelPrice) / 100;

                        Log.d("COST", "Distanță: " + distanceKm + " km");
                        Log.d("COST", "Consum: " + vehicle.getFuelConsumption() + " L/100km");
                        Log.d("COST", "Preț combustibil: " + fuelPrice + " RON/L");
                        Log.d("COST", "Cost estimat: " + estimatedCost + " RON");
                        Log.d("COST", "Timp estimat: " + estimatedTimeMinutes);

                        // Acum trimite livrarea cu estimated_cost și estimated_time
                        Log.d("COST", "Calling saveDelivery with time: " + estimatedTimeMinutes + ", cost: " + estimatedCost);

                        saveDelivery(estimatedTimeMinutes, estimatedCost);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Eroare la calculul costului!", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Eroare la preluarea prețului combustibilului!", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }
    private void saveDelivery(int estimatedTimeMinutes, double estimatedCost) {
        Log.d("SAVE", "estimatedTime: " + estimatedTimeMinutes);
        Log.d("SAVE", "estimatedCost: " + estimatedCost);

        String url = "http://192.168.1.245:3000/api/deliveries";

        JSONObject jsonBody = new JSONObject();
        try {

            jsonBody.put("delivery_address", etDeliveryAddress.getText().toString().trim());
            jsonBody.put("pickup_address", etPickupAddress.getText().toString().trim());
            jsonBody.put("driver_id", selectedDriver.getId());
            jsonBody.put("vehicle_id", selectedVehicle.getId());
            jsonBody.put("estimated_time", estimatedTimeMinutes);
            jsonBody.put("estimated_cost", estimatedCost);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonBody,
                response -> {
                    Toast.makeText(this, "Livrare adăugată cu succes!", Toast.LENGTH_SHORT).show();
                    finish();
                },
                error -> {
                    String errorMsg = "Eroare necunoscuta";
                    if (error.networkResponse != null) {
                        errorMsg = "Status: " + error.networkResponse.statusCode;
                        try {
                            errorMsg += " - " + new String(error.networkResponse.data);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (error.getMessage() != null) {
                        errorMsg = error.getMessage();
                    }
                    Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
                }
        ){};

        Volley.newRequestQueue(this).add(request);
    }





    private String formatDuration(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        if (hours > 0) {
            return hours + "h " + minutes + "min";
        }
        return minutes + " min";
    }

    private void geocodeAddress(String address, OnGeocodedListener listener) {
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address="
                + Uri.encode(address)
                + "&key=AIzaSyAFs2HB2OdG2X7-HbZNjkQ3HXbFrGm7IAE";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        JSONArray results = response.getJSONArray("results");
                        if (results.length() > 0) {
                            JSONObject location = results.getJSONObject(0)
                                    .getJSONObject("geometry")
                                    .getJSONObject("location");
                            double lat = location.getDouble("lat");
                            double lng = location.getDouble("lng");
                            listener.onGeocoded(new LatLng(lat, lng));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Eroare geocoding!", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }

    public interface OnGeocodedListener {
        void onGeocoded(LatLng latLng);
    }

//    private void addDelivery(String address, String weight, int driverId, int vehicleId) {
//        String url = "http://192.168.1.245:3000/api/deliveries";
//
//        JSONObject jsonBody = new JSONObject();
//        try {
//            jsonBody.put("delivery_address", address);
//            jsonBody.put("pickup_address", "Depozit central");
//            jsonBody.put("driver_id", driverId);
//            jsonBody.put("vehicle_id", vehicleId);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            return;
//        }
//
//        JsonObjectRequest request = new JsonObjectRequest(
//                Request.Method.POST,
//                url,
//                jsonBody,
//                response -> {
//                    Toast.makeText(this, "Livrare adăugată cu succes!", Toast.LENGTH_SHORT).show();
//                    finish();
//                },
//                error -> {
//                    String errorMsg = "Eroare necunoscuta";
//                    if (error.networkResponse != null) {
//                        errorMsg = "Status: " + error.networkResponse.statusCode;
//                        try {
//                            errorMsg += " - " + new String(error.networkResponse.data);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    } else if (error.getMessage() != null) {
//                        errorMsg = error.getMessage();
//                    }
//                    Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
//                } ){};
//
//        Volley.newRequestQueue(this).add(request);
//    }
}