package com.example.Strategy.pagos;

import java.util.Date;

public class Pago {

    private final double monto;
    private final Date fecha;
    private final MetodoPago metodo;

    public Pago(double monto, MetodoPago metodo) {
        this.monto = monto;
        this.metodo = metodo;
        this.fecha = new Date();
    }

    public boolean ejecutarPago() {
        System.out.println("Monto a pagar: " + monto);
        System.out.println("Método de pago: " + metodo.getTipo());
        return metodo.pagar();
    }

    public double getMonto() {
        return monto;
    }

    public MetodoPago getMetodo() {
        return metodo;
    }

    public Date getFecha() {
        return fecha;
    }
}
