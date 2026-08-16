package com.example.dominio.reservas;

import java.util.Date;

public class PoliticaCancelacion {
    private String nombre;
    private int diasAntelacion;
    private double porcentajeReembolso;

    public PoliticaCancelacion(String nombre, int diasAntelacion, double porcentajeReembolso) {
        this.nombre = nombre;
        this.diasAntelacion = diasAntelacion;
        this.porcentajeReembolso = porcentajeReembolso;
    }

    public String getNombre() { return nombre; }
    public int getDiasAntelacion() { return diasAntelacion; }
    public double getPorcentajeReembolso() { return porcentajeReembolso; }

    public double calcularReembolso(Reserva reserva, Date fechaCancelacion) {
        long diff = reserva.getFechaInicio().getTime() - fechaCancelacion.getTime();
        long diasAntes = diff / (1000 * 60 * 60 * 24);

        if (diasAntes >= diasAntelacion) {
            return reserva.getTotal() * porcentajeReembolso;
        }
        return 0.0;
    }
}
