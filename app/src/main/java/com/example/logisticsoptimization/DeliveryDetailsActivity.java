package com.example.logisticsoptimization;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class DeliveryDetailsActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_delivery_details);

        TextView driverName=findViewById(R.id.tvDriverName);
        TextView vehicleName=findViewById(R.id.tvVehicleName);
        TextView plate=findViewById(R.id.tvVehiclePlate);
        TextView pickupAdress=findViewById(R.id.tvPickupAddress);
        TextView deliveryAdress=findViewById(R.id.tvDeliveryAddress);
        TextView assignmentDate=findViewById(R.id.tvAssignmentDate);
        TextView deadline=findViewById(R.id.tvDeadline);
        TextView estimatedTime=findViewById(R.id.tvEstimatedTime);
        TextView estimatedCost=findViewById(R.id.tvEstimatedCost);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        int deliveryId = getIntent().getIntExtra("deliveryId", -1);
        String url = "http://192.168.0.193:3000/api/deliveries/"+deliveryId;
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {

                            JSONObject obj = response.getJSONObject(0);

//
                          driverName.setText(obj.getString("name"));
                          vehicleName.setText(obj.getString("model"));
                          plate.setText(obj.getString("plate_number"));
                          pickupAdress.setText(obj.getString("pickup_address"));
                          deliveryAdress.setText(obj.getString("delivery_address"));
                          assignmentDate.setText(obj.getString("assignment_date"));
                          deadline.setText(obj.getString("deadline"));
                          estimatedTime.setText(obj.getString("estimated_time"));
                          estimatedCost.setText(obj.getString("estimated_cost"));


                    } catch ( JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.e("DELIVERIES", "Eroare: " + error.toString());
                    Toast.makeText(this, "Eroare la incarcarea livrarilor", Toast.LENGTH_SHORT).show();
                }
        ){}; Volley.newRequestQueue(this).add(request);



    }
}