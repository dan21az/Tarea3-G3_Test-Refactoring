package com.example.Composite;
import com.example.dominio.reservas.RangoFechas;

import com.example.State.*;

public class Unidad implements CompPropiedad {

    private String idUnidad;
    private String tipo;
    private double precio;
    private EstadoUnidad estado;
    private Propiedad propiedad;

    public Unidad(String idUnidad, String tipo, double precio) {
        this.idUnidad = idUnidad;
        this.tipo = tipo;
        this.precio = precio;
        this.estado = new Disponible(); // estado inicial
    }

    public void cambiarEstado(EstadoUnidad nuevoEstado) {
        this.estado = nuevoEstado;

        System.out.println("Estado cambiado en unidad " + idUnidad);
    }

    public boolean esDisponible(RangoFechas fechas) {
        System.out.println("Verificando disponibilidad...");

        return estado instanceof Disponible;
    }

    @Override
    public double costo() {
        return precio;
    }

    public String getTipo() {
        return tipo;
    }

    public double getPrecio() {
        return precio;
    }

    public String getIdUnidad() {
        return idUnidad;
    }

    public EstadoUnidad getEstado() {
        return estado;
    }

    public Propiedad getPropiedad() {
        return propiedad;
    }

    public void setPropiedad(Propiedad propiedad) {
        this.propiedad = propiedad;
    }

    public void ocupar() {
        estado.ocupar(this);
    }

    public void liberar() {
        estado.liberar(this);
    }
}
