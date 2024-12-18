package com.example.conductordesign;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CreateDriverActivity extends AppCompatActivity {

    private EditText driverNameInput, driverPhoneInput;
    private Spinner driverStatusSpinner;
    private Button saveDriverButton, cancelButton;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_driver);

        // Inicializar vistas
        driverNameInput = findViewById(R.id.driverNameInput);
        driverPhoneInput = findViewById(R.id.driverPhoneInput);
        driverStatusSpinner = findViewById(R.id.driverStatusSpinner);
        saveDriverButton = findViewById(R.id.saveDriverButton);
        cancelButton = findViewById(R.id.cancelButton);
        databaseHelper = new DatabaseHelper(this);

        // Configurar el Spinner con opciones
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.driver_status_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        driverStatusSpinner.setAdapter(adapter);

        // Configurar botón de "Guardar"
        saveDriverButton.setOnClickListener(view -> {
            String name = driverNameInput.getText().toString().trim();
            String phone = driverPhoneInput.getText().toString().trim();
            String status = driverStatusSpinner.getSelectedItem().toString(); // Obtener el estado seleccionado

            if (name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                databaseHelper.createDriver(name, phone, status, new Callback<Boolean>() {
                    @Override
                    public void onComplete(Boolean success) {
                        runOnUiThread(() -> {
                            if (success) {
                                Toast.makeText(CreateDriverActivity.this, "Chofer registrado exitosamente", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(CreateDriverActivity.this, AdminDriverActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(CreateDriverActivity.this, "Error al registrar chofer", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        runOnUiThread(() -> Toast.makeText(CreateDriverActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                });
            }
        });

        // Configurar botón de "Cancelar"
        cancelButton.setOnClickListener(view -> {
            Intent intent = new Intent(CreateDriverActivity.this, AdminDriverActivity.class);
            startActivity(intent);
            finish();
        });
    }
}


