package com.example.logisticsoptimization;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    TextInputEditText usernameInputText;
    TextInputEditText passwordInputText;
    MaterialButton btnLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnLogin=findViewById(R.id.loginButton);
        usernameInputText = findViewById(R.id.usernameEditText);
        passwordInputText = findViewById(R.id.passwordEditText);

        btnLogin.setOnClickListener(view -> {
            loginUser();
        });



    }

    private void loginUser() {
        String username = usernameInputText.getText().toString().trim();
        String password = passwordInputText.getText().toString().trim();

        if(username.isEmpty()){
            usernameInputText.setError("Username cannot be empty");
            usernameInputText.requestFocus();
            return;
        }
        if(password.isEmpty()){
            passwordInputText.setError("Password cannot be empty");
            passwordInputText.requestFocus();
            return;
        }

        JSONObject jsonBody = new JSONObject();
        try{
            jsonBody.put("username", username);
            jsonBody.put("password", password);
        }catch (Exception e){
            e.printStackTrace();
            return;
        }
        String url = "http://192.168.0.193:3000/api/login";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonBody,
                response -> {

                    try {
                        String role = response.getJSONObject("user").getString("role");
                        String name = response.getJSONObject("user").getString("name");
                        int userId = response.getJSONObject("user").getInt("id");


                        if (role.equals("manager")) {
                            Intent intent = new Intent(MainActivity.this, ManagerDashboardActivity.class);
                            intent.putExtra("username", name);
                            startActivity(intent);
                            finish();
                        } else if (role.equals("dispatcher")) {
                            Intent intent = new Intent(MainActivity.this, DispatcherDashboardActivity.class);
                            intent.putExtra("username", name);
                            startActivity(intent);
                            finish();
                        } else if (role.equals("driver")) {
                            Intent intent = new Intent(MainActivity.this, DriverDashboardActivity.class);
                            intent.putExtra("username", name);
                            intent.putExtra("userId", userId);
                            startActivity(intent);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(MainActivity.this, "Username sau parola incorecte", Toast.LENGTH_SHORT).show();

                }
        ) {};
        Volley.newRequestQueue(this).add(request);
    }
}