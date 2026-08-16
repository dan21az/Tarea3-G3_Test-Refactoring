package com.example.dominio.reservas;

import java.util.Date;
import com.example.Composite.Unidad;
import com.example.dominio.usuarios.Huesped;

public class Reserva {
    private Date fechaInicio;
    private Date fechaFin;
    private EstadoReserva estado;
    private double total;
    private Huesped huesped;
    private Unidad unidad;
    private PoliticaCancelacion politicaCancelacion;

    public Reserva(Date fechaInicio, Date fechaFin, Huesped huesped, Unidad unidad) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.huesped = huesped;
        this.unidad = unidad;
        this.estado = EstadoReserva.PENDIENTE;
    }

    public void confirmar() {
        estado = EstadoReserva.CONFIRMADA;
        System.out.println("Reserva confirmada.");
    }

    public void marcarCancelada() {
        estado = EstadoReserva.CANCELADA;
    }

    public PoliticaCancelacion getPoliticaCancelacion() {
        if (politicaCancelacion != null) return politicaCancelacion;
        if (unidad != null && unidad.getPropiedad() != null) {
            return unidad.getPropiedad().getPoliticaCancelacion();
        }
        return null;
    }

    public void setPoliticaCancelacion(PoliticaCancelacion politicaCancelacion) {
        this.politicaCancelacion = politicaCancelacion;
    }

    public EstadoReserva getEstado() { return estado; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public Date getFechaInicio() { return fechaInicio; }
    public Date getFechaFin() { return fechaFin; }
    public Unidad getUnidad() { return unidad; }
    public Huesped getHuesped() { return huesped; }
}
