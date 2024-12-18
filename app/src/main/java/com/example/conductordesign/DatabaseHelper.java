package com.example.conductordesign;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ConductorApp.db";
    private static final int DATABASE_VERSION = 8;

    // Tabla Usuarios
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_USERTYPE = "usertype";

    // Ejecutores para tareas en segundo plano
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "email TEXT UNIQUE," +
                "password TEXT," +
                "usertype TEXT)";
        db.execSQL(CREATE_USERS_TABLE);

        String CREATE_DRIVERS_TABLE = "CREATE TABLE drivers (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "phone TEXT," +
                "status TEXT)";
        db.execSQL(CREATE_DRIVERS_TABLE);

        String CREATE_REQUESTS_TABLE = "CREATE TABLE requests (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "userId INTEGER," +
                "ubicacionInicio TEXT," +
                "destinoFinal TEXT," +
                "precio REAL," +
                "driverName TEXT," + // Nuevo campo para el nombre del chofer
                "FOREIGN KEY(userId) REFERENCES users(id))";
        db.execSQL(CREATE_REQUESTS_TABLE);


        // Agregar superusuario predeterminado
        String INSERT_ADMIN_USER = "INSERT INTO users (name, email, password, usertype) " +
                "VALUES ('Admin', 'admin@admin.com', 'admin123', 'Administrador')";
        db.execSQL(INSERT_ADMIN_USER);
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS drivers");
        db.execSQL("DROP TABLE IF EXISTS requests");
        onCreate(db);
    }


    public void registerUser(String name, String email, String password, String userType, Callback<Boolean> callback) {
        executorService.execute(() -> {
            try {
                SQLiteDatabase db = this.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("name", name);
                values.put("email", email);
                values.put("password", password);
                values.put("usertype", userType);

                long result = db.insert("users", null, values);
                if (result != -1) {
                    callback.onComplete(true); // Registro exitoso
                } else {
                    callback.onComplete(false); // Registro fallido
                }
            } catch (Exception e) {
                callback.onError(e); // Manejar error llamando a onError
            }
        });
    }


    public void validateLogin(String email, String password, Callback<Boolean> callback) {
        executorService.execute(() -> {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.query(TABLE_USERS, null,
                    COLUMN_EMAIL + "=? AND " + COLUMN_PASSWORD + "=?",
                    new String[]{email, password}, null, null, null);
            boolean isValid = cursor.getCount() > 0;
            cursor.close();
            callback.onComplete(isValid);
        });
    }
    public void getAllUsers(Callback<ArrayList<User>> callback) {
        executorService.execute(() -> {
            ArrayList<User> userList = new ArrayList<>();
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.query("users", null, null, null, null, null, null);

            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
                    userList.add(new User(id, name, email));
                } while (cursor.moveToNext());
            }
            cursor.close();
            callback.onComplete(userList);
        });
    }

    public void getUserType(String email, String password, Callback<String> callback) {
        executorService.execute(() -> {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.query("users", new String[]{"usertype"},
                    "email=? AND password=?", new String[]{email, password},
                    null, null, null);

            String userType = null;
            if (cursor != null && cursor.moveToFirst()) {
                userType = cursor.getString(cursor.getColumnIndexOrThrow("usertype"));
                cursor.close();
            }
            callback.onComplete(userType);
        });
    }



    public void deleteUser(int userId, Callback<Boolean> callback) {
        executorService.execute(() -> {
            SQLiteDatabase db = this.getWritableDatabase();
            int result = db.delete("users", "id=?", new String[]{String.valueOf(userId)});
            callback.onComplete(result > 0);
        });
    }

    public void updateUser(int userId, String newName, String newEmail, Callback<Boolean> callback) {
        executorService.execute(() -> {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("name", newName);
            values.put("email", newEmail);
            int rowsAffected = db.update("users", values, "id=?", new String[]{String.valueOf(userId)});
            callback.onComplete(rowsAffected > 0);
        });
    }



    // Obtener todos los choferes
    public void getAllDrivers(Callback<ArrayList<Driver>> callback) {
        executorService.execute(() -> {
            ArrayList<Driver> driverList = new ArrayList<>();
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.query("drivers", null, null, null, null, null, null);

            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));
                    String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
                    driverList.add(new Driver(id, name, phone, status));
                } while (cursor.moveToNext());
            }
            cursor.close();
            callback.onComplete(driverList);
        });
    }

    // Crear chofer
    public void createDriver(String name, String phone, String status, Callback<Boolean> callback) {
        executorService.execute(() -> {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("name", name);
            values.put("phone", phone);
            values.put("status", status);
            long result = db.insert("drivers", null, values);
            callback.onComplete(result != -1);
        });
    }


    // Eliminar chofer
    public void deleteDriver(int driverId, Callback<Boolean> callback) {
        executorService.execute(() -> {
            SQLiteDatabase db = this.getWritableDatabase();
            int result = db.delete("drivers", "id=?", new String[]{String.valueOf(driverId)});
            callback.onComplete(result > 0);
        });
    }

    // Editar chofer
    public void updateDriver(int driverId, String name, String phone, String status, Callback<Boolean> callback) {
        executorService.execute(() -> {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("name", name);
            values.put("phone", phone);
            values.put("status", status);
            int result = db.update("drivers", values, "id=?", new String[]{String.valueOf(driverId)});
            callback.onComplete(result > 0);
        });
    }


    // Crear una solicitud
    public void createRequest(int userId, String ubicacionInicio, String destinoFinal, double precio, String driverName, Callback<Boolean> callback) {
        executorService.execute(() -> {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("userId", userId);
            values.put("ubicacionInicio", ubicacionInicio);
            values.put("destinoFinal", destinoFinal);
            values.put("precio", precio);
            values.put("driverName", driverName);
            long result = db.insert("requests", null, values);
            if (result != -1) {
                Log.d("DatabaseHelper", "Request created successfully with ID: " + result);
            }
            callback.onComplete(result != -1);
        });
    }



    // Obtener todas las solicitudes de un usuario
    public void getAllRequests(int userId, Callback<ArrayList<Request>> callback) {
        executorService.execute(() -> {
            ArrayList<Request> requestList = new ArrayList<>();
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.query("requests", null, "userId=?",
                    new String[]{String.valueOf(userId)}, null, null, null);

            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    String ubicacionInicio = cursor.getString(cursor.getColumnIndexOrThrow("ubicacionInicio"));
                    String destinoFinal = cursor.getString(cursor.getColumnIndexOrThrow("destinoFinal"));
                    double precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"));
                    String driverName = cursor.getString(cursor.getColumnIndexOrThrow("driverName"));
                    Log.d("DatabaseHelper", "Request found: " + id + " - " + ubicacionInicio);
                    requestList.add(new Request(id, ubicacionInicio, destinoFinal, precio, driverName));
                } while (cursor.moveToNext());
            } else {
                Log.d("DatabaseHelper", "No requests found for userId: " + userId);
            }
            cursor.close();
            callback.onComplete(requestList);
        });
    }



    // Eliminar una solicitud
    public void deleteRequest(int requestId, Callback<Boolean> callback) {
        executorService.execute(() -> {
            SQLiteDatabase db = this.getWritableDatabase();
            int result = db.delete("requests", "id=?", new String[]{String.valueOf(requestId)});
            callback.onComplete(result > 0);
        });
    }


    // Metodo para actualizar el estado del chofer
    public void updateDriverStatus(String driverName, String newStatus, Callback<Boolean> callback) {
        executorService.execute(() -> {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("status", newStatus);
            int result = db.update("drivers", values, "name = ?", new String[]{driverName});
            callback.onComplete(result > 0);
        });
    }


    public void updateDriverStatusToAvailable(String driverName, Callback<Boolean> callback) {
        executorService.execute(() -> {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("status", "Disponible");
            int rows = db.update("drivers", values, "name = ?", new String[]{driverName});
            callback.onComplete(rows > 0);
        });
    }





}

