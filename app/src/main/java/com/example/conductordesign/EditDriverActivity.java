package com.example.conductordesign;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EditDriverActivity extends AppCompatActivity {

    private EditText driverNameInput, driverPhoneInput;
    private Spinner driverStatusSpinner;
    private Button saveChangesButton;
    private DatabaseHelper databaseHelper;
    private int driverId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_driver);

        driverNameInput = findViewById(R.id.driverNameInput);
        driverPhoneInput = findViewById(R.id.driverPhoneInput);
        driverStatusSpinner = findViewById(R.id.driverStatusSpinner);
        saveChangesButton = findViewById(R.id.saveChangesButton);
        databaseHelper = new DatabaseHelper(this);

        // Configurar Spinner con adaptador
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.driver_status_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        driverStatusSpinner.setAdapter(adapter);

        // Obtener datos del chofer desde el Intent
        Intent intent = getIntent();
        driverId = intent.getIntExtra("driverId", -1);
        String driverName = intent.getStringExtra("driverName");
        String driverPhone = intent.getStringExtra("driverPhone");
        String driverStatus = intent.getStringExtra("driverStatus");

        // Configurar campos de entrada con los datos actuales
        driverNameInput.setText(driverName);
        driverPhoneInput.setText(driverPhone);

        // Seleccionar el estado actual en el Spinner
        if (driverStatus != null) {
            int position = adapter.getPosition(driverStatus);
            driverStatusSpinner.setSelection(position);
        }

        saveChangesButton.setOnClickListener(view -> {
            String newName = driverNameInput.getText().toString();
            String newPhone = driverPhoneInput.getText().toString();
            String newStatus = driverStatusSpinner.getSelectedItem().toString();

            if (newName.isEmpty() || newPhone.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                // Actualizar chofer utilizando el método con Callback
                databaseHelper.updateDriver(driverId, newName, newPhone, newStatus, new Callback<Boolean>() {
                    @Override
                    public void onComplete(Boolean success) {
                        runOnUiThread(() -> {
                            if (success) {
                                Toast.makeText(EditDriverActivity.this, "Chofer actualizado exitosamente", Toast.LENGTH_SHORT).show();
                                // Volver a la actividad de administración
                                Intent backIntent = new Intent(EditDriverActivity.this, AdminDriverActivity.class);
                                startActivity(backIntent);
                                finish();
                            } else {
                                Toast.makeText(EditDriverActivity.this, "Error al actualizar chofer", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        runOnUiThread(() -> {
                            Toast.makeText(EditDriverActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            }
        });
    }
}
