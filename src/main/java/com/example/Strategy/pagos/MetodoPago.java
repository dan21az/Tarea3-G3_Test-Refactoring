package com.example.Strategy.pagos;

public interface MetodoPago {

    String getTipo();

    String getDescripcion();

    boolean pagar();
}