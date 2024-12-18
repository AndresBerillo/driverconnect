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

public class AdminUserActivity extends AppCompatActivity {

    private Button backToDashboardButton, addUserButton;
    private ListView userListView;
    private DatabaseHelper databaseHelper;
    private UserAdapter userAdapter;
    private ArrayList<User> userList;

    private ExecutorService executorService; // Executor para ejecutar tareas en segundo plano

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user);

        // Inicializar componentes
        backToDashboardButton = findViewById(R.id.backToDashboardButton);
        addUserButton = findViewById(R.id.addUserButton);
        userListView = findViewById(R.id.userListView);
        databaseHelper = new DatabaseHelper(this);

        // Inicializar el Executor
        executorService = Executors.newSingleThreadExecutor();

        // Cargar la lista de usuarios
        loadUsers();

        // Botón para regresar al Dashboard de administrador
        backToDashboardButton.setOnClickListener(view -> {
            Intent intent = new Intent(AdminUserActivity.this, AdminDashboardActivity.class);
            startActivity(intent);
            finish();
        });

        // Botón para registrar un nuevo usuario
        addUserButton.setOnClickListener(view -> {
            Intent intent = new Intent(AdminUserActivity.this, RegisterActivity.class);
            intent.putExtra("source", "admin");
            startActivity(intent);
        });
    }

    private void loadUsers() {
        executorService.execute(() -> {
            // Consultar la lista de usuarios en segundo plano
            databaseHelper.getAllUsers(new Callback<ArrayList<User>>() {
                @Override
                public void onComplete(ArrayList<User> result) {
                    runOnUiThread(() -> {
                        userList = result; // Obtener lista de usuarios
                        if (userAdapter == null) {
                            userAdapter = new UserAdapter(AdminUserActivity.this, userList, databaseHelper);
                            userListView.setAdapter(userAdapter);
                        } else {
                            userAdapter.clear();
                            userAdapter.addAll(userList);
                            userAdapter.notifyDataSetChanged();
                        }
                    });
                }

                @Override
                public void onError(Exception e) {
                    runOnUiThread(() -> {
                        Toast.makeText(AdminUserActivity.this, "Error al cargar usuarios", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUsers(); // Volver a cargar la lista cuando se retome la actividad
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown(); // Apagar el Executor al cerrar la actividad
        }
    }
}


