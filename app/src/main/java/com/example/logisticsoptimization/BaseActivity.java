package com.example.logisticsoptimization;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;

public class BaseActivity extends AppCompatActivity {

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> onBackPressed());

            // Inflează meniul în toolbar
            toolbar.inflateMenu(R.menu.top_app_bar_menu);

            // Listener pentru item-urile din meniu
            toolbar.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_logout) {
                    logout();
                    return true;
                }
                return false;
            });
        }
    }

    private void logout() {
        // Șterge userId din SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        prefs.edit().clear().apply();

        // Revino la LoginActivity
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}