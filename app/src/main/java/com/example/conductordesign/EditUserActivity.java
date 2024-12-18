package com.example.conductordesign;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EditUserActivity extends AppCompatActivity {

    private EditText userNameInput, userEmailInput;
    private Button saveChangesButton;
    private DatabaseHelper databaseHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        // Inicialización de vistas
        userNameInput = findViewById(R.id.userNameInput);
        userEmailInput = findViewById(R.id.userEmailInput);
        saveChangesButton = findViewById(R.id.saveChangesButton);
        databaseHelper = new DatabaseHelper(this);

        // Obtener datos pasados desde AdminUserActivity
        Intent intent = getIntent();
        userId = intent.getIntExtra("userId", -1);
        String userName = intent.getStringExtra("userName");
        String userEmail = intent.getStringExtra("userEmail");

        // Prellenar los campos con los datos existentes
        userNameInput.setText(userName);
        userEmailInput.setText(userEmail);

        // Listener del botón "Guardar Cambios"
        saveChangesButton.setOnClickListener(view -> {
            String newName = userNameInput.getText().toString().trim();
            String newEmail = userEmailInput.getText().toString().trim();

            if (newName.isEmpty() || newEmail.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                // Llamar al metodo asíncrono para actualizar el usuario
                databaseHelper.updateUser(userId, newName, newEmail, new Callback<Boolean>() {
                    @Override
                    public void onComplete(Boolean success) {
                        runOnUiThread(() -> {
                            if (success) {
                                Toast.makeText(EditUserActivity.this, "Usuario actualizado exitosamente", Toast.LENGTH_SHORT).show();
                                // Redirigir de nuevo a AdminUserActivity
                                Intent backIntent = new Intent(EditUserActivity.this, AdminUserActivity.class);
                                startActivity(backIntent);
                                finish();
                            } else {
                                Toast.makeText(EditUserActivity.this, "Error al actualizar usuario", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        runOnUiThread(() -> {
                            Toast.makeText(EditUserActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            }
        });
    }
}

