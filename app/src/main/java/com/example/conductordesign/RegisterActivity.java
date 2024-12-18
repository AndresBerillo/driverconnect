package com.example.conductordesign;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.conductordesign.Callback;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameInput, emailInput, passwordInput;
    private Button registerButton, cancelButton;
    private DatabaseHelper databaseHelper;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inicializar vistas
        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        registerButton = findViewById(R.id.registerButton);
        cancelButton = findViewById(R.id.cancelButton);

        // Inicializar helpers
        databaseHelper = new DatabaseHelper(this);
        executorService = Executors.newSingleThreadExecutor();

        // Obtener el contexto desde donde se inició el registro
        String source = getIntent().getStringExtra("source");

        registerButton.setOnClickListener(view -> {
            String name = nameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            // Validar los campos
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Registro asíncrono usando ExecutorService y Callback
            databaseHelper.registerUser(name, email, password, "Usuario", new Callback<Boolean>() {
                @Override
                public void onComplete(Boolean success) {
                    runOnUiThread(() -> {
                        if (success) {
                            Toast.makeText(RegisterActivity.this, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show();

                            // Redirigir según el contexto
                            if ("login".equals(source)) {
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                startActivity(intent);
                            } else if ("admin".equals(source)) {
                                Intent intent = new Intent(RegisterActivity.this, AdminUserActivity.class);
                                startActivity(intent);
                            }
                            finish(); // Finalizar esta actividad
                        } else {
                            Toast.makeText(RegisterActivity.this, "Error al registrar usuario. ¿Correo ya existente?", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onError(Exception e) {
                    runOnUiThread(() -> {
                        Toast.makeText(RegisterActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            });
        });

        // Configurar botón "Cancelar"
        cancelButton.setOnClickListener(view -> {
            if ("login".equals(source)) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
            } else if ("admin".equals(source)) {
                Intent intent = new Intent(RegisterActivity.this, AdminUserActivity.class);
                startActivity(intent);
            }
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}

