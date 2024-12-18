package com.example.conductordesign;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdminDriverActivity extends AppCompatActivity {

    private Button backToDashboardButton, addDriverButton;
    private ListView driverListView;
    private DatabaseHelper databaseHelper;
    private DriverAdapter driverAdapter;
    private ArrayList<Driver> driverList;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_driver);

        // Inicialización de vistas
        backToDashboardButton = findViewById(R.id.backToDashboardButton);
        addDriverButton = findViewById(R.id.addDriverButton);
        driverListView = findViewById(R.id.driverListView);

        databaseHelper = new DatabaseHelper(this);
        executorService = Executors.newFixedThreadPool(4);

        loadDrivers();

        backToDashboardButton.setOnClickListener(view -> {
            Intent intent = new Intent(AdminDriverActivity.this, AdminDashboardActivity.class);
            startActivity(intent);
            finish();
        });

        addDriverButton.setOnClickListener(view -> {
            Intent intent = new Intent(AdminDriverActivity.this, CreateDriverActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Carga la lista de choferes de manera asíncrona usando Executor y Callback.
     */
    private void loadDrivers() {
        executorService.execute(() -> {
            databaseHelper.getAllDrivers(new Callback<ArrayList<Driver>>() {
                @Override
                public void onComplete(ArrayList<Driver> drivers) {
                    runOnUiThread(() -> {
                        driverList = drivers;
                        if (driverAdapter == null) {
                            driverAdapter = new DriverAdapter(AdminDriverActivity.this, driverList, databaseHelper);
                            driverListView.setAdapter(driverAdapter);
                        } else {
                            driverAdapter.clear();
                            driverAdapter.addAll(driverList);
                            driverAdapter.notifyDataSetChanged();
                        }
                    });
                }

                @Override
                public void onError(Exception e) {
                    runOnUiThread(() -> Toast.makeText(AdminDriverActivity.this, "Error al cargar choferes", Toast.LENGTH_SHORT).show());
                }
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDrivers(); // Recarga la lista al volver a la actividad
    }
}
