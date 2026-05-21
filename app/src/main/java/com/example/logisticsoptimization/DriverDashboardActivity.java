package com.example.logisticsoptimization;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
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
import org.json.JSONArray;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.example.logisticsoptimization.Delivery;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DriverDashboardActivity extends BaseActivity {
    TextView tvWelcome;
    RecyclerView recyclerViewDeliveries;
    List<Delivery> deliveryList = new ArrayList<>();
    DeliveryAdapter adapter;
    Delivery selectedDelivery = null;
    MaterialButton btnStartDelivery;
    Button btnChangePass;
    int userId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_driver_dashboard);

        tvWelcome = findViewById(R.id.tvWelcome);
        btnStartDelivery = findViewById(R.id.btnStartDelivery);
        btnChangePass=findViewById(R.id.changePassButton);


        String username = getIntent().getStringExtra("username");
        userId = getIntent().getIntExtra("userId", -1);

        if(username != null){
            tvWelcome.setText("Welcome, " + username + "!");
        }
        
        recyclerViewDeliveries = findViewById(R.id.recyclerViewDeliveries);

        adapter = new DeliveryAdapter(this, deliveryList, new DeliveryAdapter.OnDeliveryClickListener() {
            @Override
            public void onDeliveryClick(Delivery delivery) {
                selectedDelivery = delivery;
            }

            @Override
            public void onViewDetailsClick(Delivery delivery) {
                Intent intent = new Intent(DriverDashboardActivity.this, DeliveryDetailsActivity.class);
                intent.putExtra("deliveryId", delivery.getId());
                intent.putExtra("pickupAddress", delivery.getPickupAddress());
                intent.putExtra("deliveryAddress", delivery.getDeliveryAddress());
                intent.putExtra("deadline", delivery.getDeadline());
                startActivity(intent);
            }
        });
        
        recyclerViewDeliveries.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDeliveries.setAdapter(adapter);
        
        loadDeliveries();


        btnStartDelivery.setOnClickListener(v -> {
            if (selectedDelivery != null) {
                Intent intent = new Intent(DriverDashboardActivity.this, ActiveDeliveryActivity.class);
                intent.putExtra("deliveryId", selectedDelivery.getId());
                intent.putExtra("pickupAddress", selectedDelivery.getPickupAddress());
                intent.putExtra("deliveryAddress", selectedDelivery.getDeliveryAddress());
                intent.putExtra("deadline", selectedDelivery.getDeadline());
                intent.putExtra("fuelConsumption", selectedDelivery.getFuelConsumption());
                intent.putExtra("fuelType", selectedDelivery.getFuelType());
                intent.putExtra("fuelPrice", selectedDelivery.getFuelPrice());
                startActivity(intent);
            } else {
                Toast.makeText(this, "Please select a delivery first", Toast.LENGTH_SHORT).show();
            }
        });

        btnChangePass.setOnClickListener(view -> {
            SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
            int userId = preferences.getInt("userId", 0);

            if (userId == 0) {
                Toast.makeText(this, "Trebuie să te loghezi mai întâi!", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(DriverDashboardActivity.this, ChangePasswordActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadDeliveries() {
        String url = "http://192.168.1.245:3000/api/deliveries/driver/" + userId;

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        deliveryList.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            Delivery delivery = new Delivery(
                                    obj.getInt("id"),
                                    obj.getString("pickup_address"),
                                    obj.getString("delivery_address"),
                                    obj.getString("deadline"),
                                    obj.getDouble("fuel_consumption"),
                                    obj.getString("fuel_type"),
                                    obj.getDouble("fuel_price")
                            );
                            deliveryList.add(delivery);
                        }
                        adapter.notifyDataSetChanged();

                    } catch ( JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.e("DELIVERIES", "Eroare: " + error.toString());
                    Toast.makeText(this, "Eroare la incarcarea livrarilor", Toast.LENGTH_SHORT).show();
                }
        ) {};

        Volley.newRequestQueue(this).add(request);
    }
}