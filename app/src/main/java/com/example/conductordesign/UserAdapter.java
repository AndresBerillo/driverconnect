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

import java.util.ArrayList;

public class UserAdapter extends ArrayAdapter<User> {

    private Context context;
    private ArrayList<User> users;
    private DatabaseHelper databaseHelper;

    public UserAdapter(Context context, ArrayList<User> users, DatabaseHelper databaseHelper) {
        super(context, 0, users);
        this.context = context;
        this.users = users;
        this.databaseHelper = databaseHelper;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        }

        User user = users.get(position);

        TextView nameTextView = convertView.findViewById(R.id.userNameTextView);
        TextView emailTextView = convertView.findViewById(R.id.userEmailTextView);
        Button deleteButton = convertView.findViewById(R.id.deleteUserButton);
        Button editButton = convertView.findViewById(R.id.editUserButton);

        nameTextView.setText(user.getName());
        emailTextView.setText(user.getEmail());

        // Configurar el botón de eliminar con Callback
        deleteButton.setOnClickListener(view -> {
            databaseHelper.deleteUser(user.getId(), new Callback<Boolean>() {
                @Override
                public void onComplete(Boolean success) {
                    // Actualizar UI en el hilo principal
                    ((AdminUserActivity) context).runOnUiThread(() -> {
                        if (success) {
                            users.remove(user);
                            notifyDataSetChanged();
                            Toast.makeText(context, "Usuario eliminado", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Error al eliminar usuario", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onError(Exception e) {
                    ((AdminUserActivity) context).runOnUiThread(() -> {
                        Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            });
        });

        // Configurar el botón de editar
        editButton.setOnClickListener(view -> {
            Intent intent = new Intent(context, EditUserActivity.class);
            intent.putExtra("userId", user.getId());
            intent.putExtra("userName", user.getName());
            intent.putExtra("userEmail", user.getEmail());
            context.startActivity(intent);
        });

        return convertView;
    }
}
