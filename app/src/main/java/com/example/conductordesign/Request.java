package com.example.conductordesign;

public class Request {
    private int id;
    private String ubicacionInicio;
    private String destinoFinal;
    private double precio;
    private String driverName;
    public Request(int id, String ubicacionInicio, String destinoFinal, double precio, String driverName) {
        this.id = id;
        this.ubicacionInicio = ubicacionInicio;
        this.destinoFinal = destinoFinal;
        this.precio = precio;
        this.driverName = driverName;
    }

    public String getDriverName() {
        return driverName;
    }

    public int getId() {
        return id;
    }

    public String getUbicacionInicio() {
        return ubicacionInicio;
    }

    public String getDestinoFinal() {
        return destinoFinal;
    }

    public double getPrecio() {
        return precio;
    }
}
