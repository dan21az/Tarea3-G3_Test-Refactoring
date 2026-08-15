package com.example.dominio.incidentes;

import java.util.concurrent.atomic.AtomicLong;

import com.example.dominio.reservas.Reserva;

public class Incidente {

    private static final AtomicLong SECUENCIA = new AtomicLong(1);

    private String idIncidente;
    private String descripcion;
    private EstadoIncidente estado;
    private Reserva reserva;

    public Incidente(String descripcion, Reserva reserva) {
        this(generarIdAutomatico(), descripcion, reserva);
    }

    public Incidente(String idIncidente,
                     String descripcion,
                     Reserva reserva) {

        this.idIncidente = idIncidente;
        this.descripcion = descripcion;
        this.reserva = reserva;
        this.estado = EstadoIncidente.ABIERTO;
    }

    private static String generarIdAutomatico() {
        return "INC-" + String.format("%03d", SECUENCIA.getAndIncrement());
    }

    public void escalar() {
        if (estado == EstadoIncidente.RESUELTO || estado == EstadoIncidente.CRITICO) {
            System.out.println("El incidente ya no puede cambiar de estado: " + estado);
            return;
        }
        estado = EstadoIncidente.ESCALADO;
        System.out.println("Incidente escalado.");
    }

    public void resolver() {
        if (estado == EstadoIncidente.RESUELTO) {
            System.out.println("El incidente ya está resuelto.");
            return;
        }
        if (estado == EstadoIncidente.CRITICO) {
            System.out.println("El incidente crítico no puede resolverse desde esta acción sin revisión adicional.");
            return;
        }
        estado = EstadoIncidente.RESUELTO;
        System.out.println("Incidente resuelto.");
    }

    public void resolverConRevision() {
        if (estado == EstadoIncidente.RESUELTO) {
            System.out.println("El incidente ya está resuelto.");
            return;
        }
        EstadoIncidente previo = estado;
        estado = EstadoIncidente.RESUELTO;
        if (previo == EstadoIncidente.CRITICO) {
            System.out.println("Incidente crítico resuelto tras revisión adicional del soporte.");
        } else {
            System.out.println("Incidente resuelto.");
        }
    }

    public void cambiarEstado(EstadoIncidente nuevoEstado) {
        if (this.estado == EstadoIncidente.RESUELTO && nuevoEstado != EstadoIncidente.RESUELTO) {
            System.out.println("No se puede modificar un incidente ya resuelto.");
            return;
        }
        if (this.estado == EstadoIncidente.CRITICO && nuevoEstado == EstadoIncidente.RESUELTO) {
            System.out.println("No se puede resolver directamente un incidente crítico sin la validación del soporte.");
            return;
        }
        this.estado = nuevoEstado;
        System.out.println("Nuevo estado: " + nuevoEstado);
    }

    public EstadoIncidente getEstado() {
        return estado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getIdIncidente() {
        return idIncidente;
    }

    public Reserva getReserva() {
        return reserva;
    }
}