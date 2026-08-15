package com.example.dominio.reservas;

public class PoliticaCancelacion {

    private String nombre;
    private int diasAntelacion;
    private double porcentajeReembolso;

    public PoliticaCancelacion(String nombre, int diasAntelacion, double porcentajeReembolso) {
        this.nombre = nombre;
        this.diasAntelacion = diasAntelacion;
        this.porcentajeReembolso = porcentajeReembolso;
    }

    public String getNombre() {
        return nombre;
    }

    public int getDiasAntelacion() {
        return diasAntelacion;
    }

    public double getPorcentajePenalizacion() {
        return porcentajeReembolso;
    }
}
