package com.example.logisticsoptimization;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

public class AddVehicleActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_vehicle);
        AutoCompleteTextView fuelType= findViewById(R.id.etFuelType);
        fuelType.setOnClickListener(v -> fuelType.showDropDown());
        loadFuelTypes(fuelType);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextInputEditText model= findViewById(R.id.etVehicleModel);
        TextInputEditText capacity= findViewById(R.id.etVehicleCapacity);
        TextInputEditText fuelConsumption= findViewById(R.id.etFuelConsumption);
        TextInputEditText plateNumber= findViewById(R.id.etPlateNumber);
        TextInputEditText accidents= findViewById(R.id.etAccidents);
        MaterialButton btnSubmit = findViewById(R.id.btnSubmitVehicle);
        btnSubmit.setOnClickListener(view -> {
            btnSubmit.setOnClickListener(v -> {
                String modelVal = model.getText().toString().trim();
                String capacityVal = capacity.getText().toString().trim();
                String fuelConsumptionVal = fuelConsumption.getText().toString().trim();
                String plateNumberVal = plateNumber.getText().toString().trim();
                String accidentsVal = accidents.getText().toString().trim();
                String fuelTypeVal = fuelType.getText().toString().trim();

                if (modelVal.isEmpty() || capacityVal.isEmpty() || fuelConsumptionVal.isEmpty()
                        || plateNumberVal.isEmpty() || fuelTypeVal.isEmpty()) {
                    Toast.makeText(this, "Completează toate câmpurile obligatorii!", Toast.LENGTH_SHORT).show();
                    return;
                }

                addVehicle(modelVal, capacityVal, fuelConsumptionVal, plateNumberVal, accidentsVal, fuelTypeVal);
            });


        });

    }

    private void addVehicle(String modelVal, String capacityVal, String fuelConsumptionVal, String plateNumberVal, String accidentsVal, String fuelTypeVal) {

        String url = "http://192.168.1.245:3000/api/vehicles";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model", modelVal);
            jsonBody.put("plate_number",plateNumberVal );
            jsonBody.put("capacity_kg", capacityVal);
            jsonBody.put("fuel_consumption", fuelConsumptionVal);
            jsonBody.put("accidents", accidentsVal);
            jsonBody.put("fuel_type", fuelTypeVal);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest requestVehicle = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonBody,
                response -> {
                    Toast.makeText(this, "Vehicul adăugat cu succes!", Toast.LENGTH_SHORT).show();
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
        ) {};

        Volley.newRequestQueue(this).add(requestVehicle);

    }

    private void loadFuelTypes(AutoCompleteTextView fuelType) {

        String url = "http://192.168.1.245:3000/api/fuel-types";

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {

                    try {
                        List<String> fuelTypes = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            fuelTypes.add(response.getString(i));
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                this,
                                android.R.layout.simple_dropdown_item_1line,
                                fuelTypes
                        );
                        fuelType.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.e("FUEL_TYPES", "Eroare: " + error.toString());
                    Toast.makeText(this, "Eroare la incarcarea tipurilor de combustibil", Toast.LENGTH_SHORT).show();
                }
        );

        Volley.newRequestQueue(this).add(request);
    }




}