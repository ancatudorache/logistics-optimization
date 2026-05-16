package com.example.logisticsoptimization;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

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

        TextInputEditText etDeliveryAddress = findViewById(R.id.etDeliveryAddress);
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

            addDelivery(address, weight, selectedDriver.getId(), selectedVehicle.getId());
        });
    }

    private void loadDrivers() {
        String url = "http://192.168.0.193:3000/api/drivers";

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
        String url = "http://192.168.0.193:3000/api/vehicles";

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
                                    obj.getString("plate_number")
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

    private void addDelivery(String address, String weight, int driverId, int vehicleId) {
        String url = "http://192.168.0.193:3000/api/deliveries";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("delivery_address", address);
            jsonBody.put("pickup_address", "Depozit central");
            jsonBody.put("driver_id", driverId);
            jsonBody.put("vehicle_id", vehicleId);
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
                } ){};

        Volley.newRequestQueue(this).add(request);
    }
}