package com.example.conductordesign;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;

public class RequestAdapter extends ArrayAdapter<Request> {

    private Context context;
    private ArrayList<Request> requests;
    private DatabaseHelper databaseHelper;
    private Runnable refreshCallback;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public RequestAdapter(Context context, ArrayList<Request> requests, DatabaseHelper databaseHelper, Runnable refreshCallback) {
        super(context, 0, requests);
        this.context = context;
        this.requests = requests;
        this.databaseHelper = databaseHelper;
        this.refreshCallback = refreshCallback;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.request_item, parent, false);
        }

        if (position < 0 || position >= requests.size()) {
            return convertView;
        }

        Request request = requests.get(position);
        TextView requestInfoTextView = convertView.findViewById(R.id.requestInfoTextView);
        Button cancelRequestButton = convertView.findViewById(R.id.cancelRequestButton);
        Button completeRequestButton = convertView.findViewById(R.id.completeRequestButton);

        requestInfoTextView.setText(
                "Inicio: " + request.getUbicacionInicio() +
                        "\nDestino: " + request.getDestinoFinal() +
                        "\nPrecio: $" + request.getPrecio()
        );

        cancelRequestButton.setOnClickListener(view -> handleRequestAction(position, "cancelada"));
        completeRequestButton.setOnClickListener(view -> handleRequestAction(position, "finalizada"));

        return convertView;
    }

    private void handleRequestAction(int position, String action) {
        if (position < 0 || position >= requests.size()) {
            Toast.makeText(context, "Error: solicitud no encontrada.", Toast.LENGTH_SHORT).show();
            return;
        }

        Request request = requests.get(position);

        // Eliminar solicitud de forma asíncrona
        databaseHelper.deleteRequest(request.getId(), new Callback<Boolean>() {
            @Override
            public void onComplete(Boolean success) {
                if (success) {
                    // Actualizar el estado del chofer a "Disponible"
                    databaseHelper.updateDriverStatusToAvailable(request.getDriverName(), new Callback<Boolean>() {
                        @Override
                        public void onComplete(Boolean statusUpdated) {
                            mainHandler.post(() -> {
                                if (statusUpdated) {
                                    requests.remove(position);
                                    notifyDataSetChanged();
                                    Toast.makeText(context, "Solicitud " + action + ". Chofer disponible.", Toast.LENGTH_SHORT).show();
                                    refreshCallback.run(); // Llamar al callback para refrescar datos
                                } else {
                                    Toast.makeText(context, "Error al actualizar el estado del chofer.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onError(Exception e) {
                            mainHandler.post(() -> Toast.makeText(context, "Error al actualizar el estado del chofer: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        }
                    });
                } else {
                    mainHandler.post(() -> Toast.makeText(context, "Error al procesar la solicitud.", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onError(Exception e) {
                mainHandler.post(() -> Toast.makeText(context, "Error al eliminar la solicitud: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }
}
