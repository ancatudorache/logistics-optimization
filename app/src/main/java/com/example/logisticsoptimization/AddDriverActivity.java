package com.example.logisticsoptimization;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

public class AddDriverActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_driver);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextInputEditText etName = findViewById(R.id.etDriverName);
        TextInputEditText etCNP = findViewById(R.id.etDriverCnp);
        TextInputEditText etUsername = findViewById(R.id.etDriverUsername);
        TextInputEditText etPassword = findViewById(R.id.etDriverPassword);
        MaterialButton btnSubmit = findViewById(R.id.btnSubmitDriver);

        btnSubmit.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String CNP = etCNP.getText().toString().trim();
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (name.isEmpty() || CNP.isEmpty() || username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completează toate câmpurile!", Toast.LENGTH_SHORT).show();
                return;
            }

            addDriver(name, CNP, username, password);
        });
    }

    private void addDriver(String name, String cnp, String username, String password) {
        String url = "http://192.168.1.245:3000/api/drivers";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("name", name);
            jsonBody.put("CNP", cnp);
            jsonBody.put("username", username);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonBody,
                response -> {
                    Toast.makeText(this, "Driver adăugat cu succes!", Toast.LENGTH_SHORT).show();
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

        Volley.newRequestQueue(this).add(request);
    }
}
