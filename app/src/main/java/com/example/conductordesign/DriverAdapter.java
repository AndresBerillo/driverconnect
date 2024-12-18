package com.example.conductordesign;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class DriverAdapter extends ArrayAdapter<Driver> {

    private Context context;
    private ArrayList<Driver> drivers;
    private DatabaseHelper databaseHelper;

    public DriverAdapter(Context context, ArrayList<Driver> drivers, DatabaseHelper databaseHelper) {
        super(context, 0, drivers);
        this.context = context;
        this.drivers = drivers;
        this.databaseHelper = databaseHelper;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.driver_item, parent, false);
        }

        Driver driver = drivers.get(position);

        // Obtener referencias a las vistas
        TextView nameTextView = convertView.findViewById(R.id.driverNameTextView);
        TextView phoneTextView = convertView.findViewById(R.id.driverPhoneTextView);
        Button deleteButton = convertView.findViewById(R.id.deleteDriverButton);
        Button editButton = convertView.findViewById(R.id.editDriverButton);

        // Configurar los datos del chofer
        nameTextView.setText(driver.getName());
        phoneTextView.setText(driver.getPhone());

        // Configurar el botón de eliminar
        deleteButton.setOnClickListener(view -> {
            databaseHelper.deleteDriver(driver.getId(), new Callback<Boolean>() {
                @Override
                public void onComplete(Boolean success) {
                    // Ejecutar en el hilo principal
                    ((AppCompatActivity) context).runOnUiThread(() -> {
                        if (success) {
                            drivers.remove(driver); // Eliminar chofer de la lista
                            notifyDataSetChanged(); // Notificar cambios al adaptador
                            Toast.makeText(context, "Chofer eliminado", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Error al eliminar chofer", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onError(Exception e) {
                    ((AppCompatActivity) context).runOnUiThread(() -> {
                        Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            });
        });

        // Configurar el botón de editar
        editButton.setOnClickListener(view -> {
            Intent intent = new Intent(context, EditDriverActivity.class);
            intent.putExtra("driverId", driver.getId());
            intent.putExtra("driverName", driver.getName());
            intent.putExtra("driverPhone", driver.getPhone());
            intent.putExtra("driverStatus", driver.getStatus()); // Pasar el estado del chofer
            context.startActivity(intent);
        });

        return convertView;
    }
}


