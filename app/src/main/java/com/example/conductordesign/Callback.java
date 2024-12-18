package com.example.conductordesign;


/**
 * Interfaz genérica para manejar resultados asíncronos.
 * @param <T> El tipo de resultado a devolver (ej: Boolean, String, etc.).
 */

public interface Callback<T> {
    void onComplete(T result);
    void onError(Exception e);
}
