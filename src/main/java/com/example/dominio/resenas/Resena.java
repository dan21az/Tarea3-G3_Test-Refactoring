package com.example.dominio.resenas;

import com.example.dominio.reservas.Reserva;
import com.example.dominio.usuarios.Huesped;

public class Resena {

    private Huesped huesped;
    private String comentario;
    private int puntuacion;
    private Reserva reserva;

    public Resena(Huesped huesped, String comentario, int puntuacion, Reserva... reservas) {
        this.huesped = huesped;
        this.comentario = comentario;
        this.puntuacion = puntuacion;
        this.reserva = (reservas.length > 0) ? reservas[0] : null;
    }

    public Huesped getHuesped() {
        return huesped;
    }

    public String getComentario() {
        return comentario;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public Reserva getReserva() {
        return reserva;
    }
}
