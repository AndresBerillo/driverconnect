package com.example.conductordesign;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserDashboardActivity extends AppCompatActivity {

    private EditText startLocationInput, endLocationInput, distanceInput;
    private Button createRequestButton, logoutButton;
    private ListView requestListView;
    private Spinner driverSelectorSpinner;

    private DatabaseHelper databaseHelper;
    private ExecutorService executorService;

    private ArrayList<String> driverNames;
    private ArrayList<Request> requestList;
    private RequestAdapter requestAdapter;
    private String selectedDriverName;
    private final int userId = 1; // Asumimos que el usuario tiene ID 1 para este ejemplo.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        // Inicializar vistas
        startLocationInput = findViewById(R.id.startLocationInput);
        endLocationInput = findViewById(R.id.endLocationInput);
        distanceInput = findViewById(R.id.distanceInput);
        createRequestButton = findViewById(R.id.createRequestButton);
        logoutButton = findViewById(R.id.logoutButton);
        requestListView = findViewById(R.id.requestListView);
        driverSelectorSpinner = findViewById(R.id.driverSelectorSpinner);

        databaseHelper = new DatabaseHelper(this);
        executorService = Executors.newFixedThreadPool(4);

        // Cargar choferes y solicitudes
        loadAvailableDrivers();
        loadRequests();

        // Configurar botones
        createRequestButton.setOnClickListener(view -> createRequestFromInput());
        logoutButton.setOnClickListener(view -> logout());
    }

    private void loadAvailableDrivers() {
        executorService.execute(() -> {
            databaseHelper.getAllDrivers(new Callback<ArrayList<Driver>>() {
                @Override
                public void onComplete(ArrayList<Driver> drivers) {
                    runOnUiThread(() -> {
                        driverNames = new ArrayList<>();
                        for (Driver driver : drivers) {
                            if ("Disponible".equalsIgnoreCase(driver.getStatus())) {
                                driverNames.add(driver.getName());
                            }
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(UserDashboardActivity.this,
                                android.R.layout.simple_spinner_dropdown_item, driverNames);
                        driverSelectorSpinner.setAdapter(adapter);
                        driverSelectorSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                                selectedDriverName = driverNames.get(position);
                            }

                            @Override
                            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                                selectedDriverName = null;
                            }
                        });
                    });
                }

                @Override
                public void onError(Exception e) {
                    runOnUiThread(() -> Toast.makeText(UserDashboardActivity.this, "Error al cargar choferes", Toast.LENGTH_SHORT).show());
                }
            });
        });
    }

    private void loadRequests() {
        databaseHelper.getAllRequests(userId, new Callback<ArrayList<Request>>() {
            @Override
            public void onComplete(ArrayList<Request> requests) {
                runOnUiThread(() -> {
                    requestList = requests;
                    if (requestAdapter == null) {
                        requestAdapter = new RequestAdapter(UserDashboardActivity.this, requestList, databaseHelper, this::refreshData);
                        requestListView.setAdapter(requestAdapter);
                    } else {
                        requestAdapter.clear();
                        requestAdapter.addAll(requestList);
                        requestAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> Toast.makeText(UserDashboardActivity.this, "Error al cargar solicitudes", Toast.LENGTH_SHORT).show());
            }

            private void refreshData() {
                loadAvailableDrivers();
                loadRequests();
            }
        });
    }

    private void createRequestFromInput() {
        String ubicacionInicio = startLocationInput.getText().toString();
        String destinoFinal = endLocationInput.getText().toString();
        String distanciaText = distanceInput.getText().toString();

        if (ubicacionInicio.isEmpty() || destinoFinal.isEmpty() || distanciaText.isEmpty() || selectedDriverName == null) {
            Toast.makeText(this, "Completa todos los campos y selecciona un chofer", Toast.LENGTH_SHORT).show();
            return;
        }

        double distancia;
        try {
            distancia = Double.parseDouble(distanciaText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Distancia inválida", Toast.LENGTH_SHORT).show();
            return;
        }

        double precio = distancia * 500;

        executorService.execute(() -> {
            databaseHelper.createRequest(userId, ubicacionInicio, destinoFinal, precio, selectedDriverName, new Callback<Boolean>() {
                @Override
                public void onComplete(Boolean success) {
                    if (success) {
                        databaseHelper.updateDriverStatus(selectedDriverName, "En servicio", new Callback<Boolean>() {
                            @Override
                            public void onComplete(Boolean statusUpdated) {
                                runOnUiThread(() -> {
                                    if (statusUpdated) {
                                        Toast.makeText(UserDashboardActivity.this, "Solicitud creada exitosamente", Toast.LENGTH_SHORT).show();
                                        clearInputFields();
                                        loadAvailableDrivers();
                                        loadRequests();
                                    } else {
                                        Toast.makeText(UserDashboardActivity.this, "Error al actualizar estado del chofer", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onError(Exception e) {
                                runOnUiThread(() -> Toast.makeText(UserDashboardActivity.this, "Error al actualizar estado", Toast.LENGTH_SHORT).show());
                            }
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(UserDashboardActivity.this, "Error al crear la solicitud", Toast.LENGTH_SHORT).show());
                    }
                }

                @Override
                public void onError(Exception e) {
                    runOnUiThread(() -> Toast.makeText(UserDashboardActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            });
        });
    }

    private void clearInputFields() {
        startLocationInput.setText("");
        endLocationInput.setText("");
        distanceInput.setText("");
        driverSelectorSpinner.setSelection(0);
    }

    private void logout() {
        Intent intent = new Intent(UserDashboardActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
