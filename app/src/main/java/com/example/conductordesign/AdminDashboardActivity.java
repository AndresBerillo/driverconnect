package com.example.conductordesign;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class AdminDashboardActivity extends AppCompatActivity {

    private Button manageUsersButton, manageDriversButton, logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        manageUsersButton = findViewById(R.id.manageUsersButton);
        manageDriversButton = findViewById(R.id.manageDriversButton);
        logoutButton = findViewById(R.id.logoutButton);

        manageUsersButton.setOnClickListener(view -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AdminUserActivity.class);
            startActivity(intent);
        });

        manageDriversButton.setOnClickListener(view -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AdminDriverActivity.class);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(view -> {
            logout();
        });
    }

    private void logout() {
        Intent intent = new Intent(AdminDashboardActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}

