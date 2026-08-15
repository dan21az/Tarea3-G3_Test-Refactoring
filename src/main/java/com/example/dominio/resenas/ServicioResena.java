package com.example.dominio.resenas;

import java.util.ArrayList;
import java.util.List;

import com.example.dominio.reservas.Reserva;
import com.example.dominio.usuarios.Huesped;

public class ServicioResena {

    private List<Resena> resenas = new ArrayList<>();

    public Resena crearResena(Huesped huesped, String comentario, int puntuacion, Reserva... reservas) {
        if (huesped == null || comentario == null || comentario.isBlank()) {
            return null;
        }
        if (puntuacion < 1 || puntuacion > 5) {
            return null;
        }

        Resena resena = new Resena(huesped, comentario, puntuacion, reservas);
        resenas.add(resena);
        return resena;
    }

    public List<Resena> getResenas() {
        return resenas;
    }
}
