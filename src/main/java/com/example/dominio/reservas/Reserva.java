package com.example.dominio.reservas;

import java.util.Date;

import com.example.Composite.Unidad;
import com.example.dominio.usuarios.Huesped;

public class Reserva {

    private RangoFechas rangoFechas;
    private EstadoReserva estado;
    private double total;
    private Huesped huesped;
    private Unidad unidad;
    private PoliticaCancelacion politicaCancelacion;

    public Reserva(RangoFechas rangoFechas,
                   Huesped huesped,
                   Unidad unidad) {

        this.rangoFechas = rangoFechas;
        this.huesped = huesped;
        this.unidad = unidad;
        this.estado = EstadoReserva.PENDIENTE;
    }

    public void confirmar() {
        estado = EstadoReserva.CONFIRMADA;

        System.out.println("Reserva confirmada.");
    }

    public double cancelar() {
        estado = EstadoReserva.CANCELADA;

        double reembolso = 0.0;

        if (politicaCancelacion != null) {
            reembolso = politicaCancelacion.calcularReembolso(total);
        }

        System.out.println("Reserva cancelada. Reembolso: " + reembolso);
        return reembolso;
    }

    public PoliticaCancelacion getPoliticaCancelacion() {
        return politicaCancelacion;
    }

    public void setPoliticaCancelacion(PoliticaCancelacion politicaCancelacion) {
        this.politicaCancelacion = politicaCancelacion;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Date getFechaInicio() {
        return rangoFechas.getInicio();
    }

    public Date getFechaFin() {
        return rangoFechas.getFin();
    }

    public RangoFechas getRangoFechas() {
        return rangoFechas;
    }

    public Unidad getUnidad() {
        return unidad;
    }

    public Huesped getHuesped(){
        return huesped;
    }
}
