package com.example.logisticsoptimization;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;

public class DispatcherDashboardActivity extends BaseActivity {
    TextView tvWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dispatcher_dashboard_acivity);

        tvWelcome = findViewById(R.id.tvWelcome);
        
        String username = getIntent().getStringExtra("username");
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if(username != null && tvWelcome != null){
            tvWelcome.setText("Welcome, " + username + "!");
        }

        MaterialCardView cardAddDelivery=findViewById(R.id.cardAddDelivery);
        cardAddDelivery.setOnClickListener(v -> {
            Intent intent = new Intent(DispatcherDashboardActivity.this, AddDeliveryActivity.class);
            startActivity(intent);
        });

        MaterialCardView cardAddVehicle=findViewById(R.id.cardAddVehicle);
        cardAddVehicle.setOnClickListener(v -> {
            Intent intent = new Intent(DispatcherDashboardActivity.this, AddVehicleActivity.class);
            startActivity(intent);
        });
        MaterialCardView cardAddDriver=findViewById(R.id.cardAddDriver);
        cardAddDriver.setOnClickListener(v -> {
            Intent intent = new Intent(DispatcherDashboardActivity.this, AddDriverActivity.class);
            startActivity(intent);
        });



    }
}