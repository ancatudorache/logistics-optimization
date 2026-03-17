package com.example.logisticsoptimization;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

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
        String username = usernameInputText.getText().toString();
        String password = passwordInputText.getText().toString();

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

        // Fake login by role
        if (username.equals("dispatcher1") && password.equals("1234")) {
            Intent intent = new Intent(MainActivity.this, DispatcherDashboardActivity.class);
            intent.putExtra("username",username);
            startActivity(intent);
            finish();
        } else if (username.equals("manager1") && password.equals("1234")) {
            Intent intent = new Intent(MainActivity.this, ManagerDashboardActivity.class);
            intent.putExtra("username",username);
            startActivity(intent);
            finish();
        } else if (username.equals("driver1") && password.equals("1234")) {
            Intent intent = new Intent(MainActivity.this, DriverDashboardActivity.class);
            intent.putExtra("username",username);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
        }
    }
}