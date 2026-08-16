package com.example.dominio.usuarios;

import com.example.Composite.Propiedad;
import com.example.Composite.Unidad;
import com.example.Singleton.Repositorio;
import com.example.dominio.CriterioBusqueda;
import com.example.dominio.incidentes.Incidente;
import com.example.dominio.reservas.Reserva;

import java.util.ArrayList;
import java.util.List;
import com.example.dominio.reservas.RangoFechas;
import com.example.dominio.CriterioBusquedaEvaluator;

public class Huesped extends Usuario {

    private List<Reserva> reservas;

    public Huesped(String id, String nombre, String correo, String password) {
        super(id, nombre, correo, password);
        this.reservas = new ArrayList<>();
    }

    public List<Propiedad> buscarPropiedad(CriterioBusqueda criterio, Repositorio repositorio) {
        System.out.println(nombre + " está buscando propiedades...");
        List<Propiedad> resultados = new ArrayList<>();
        CriterioBusquedaEvaluator evaluador = new CriterioBusquedaEvaluator();

        if (repositorio != null) {
            for (Propiedad propiedad : repositorio.getCatalogo()) {
                if (evaluador.coincide(propiedad, criterio)) {
                    resultados.add(propiedad);
                }
            }
        }

        return resultados;
    }

    public Reserva realizarReserva(Unidad unidad, RangoFechas fechas) {
        System.out.println(nombre + " está realizando una reserva.");

        Reserva reserva = new Reserva(fechas, this, unidad);
        reservas.add(reserva);

        return reserva;
    }

    public boolean autenticar(String correo, String password) {
        return this.correo != null && this.correo.equals(correo) && this.password != null && this.password.equals(password);
    }

    public void registrarReserva(Reserva reserva) {
        if (reserva != null) {
            reservas.add(reserva);
        }
    }

    public Incidente reportarIncidente(String descripcion, Reserva reserva) {
        System.out.println(nombre + " reportó incidente: " + descripcion);

        return new Incidente(descripcion, reserva);
    }

    public List<Reserva> historialReservas() {
        System.out.println("Mostrando historial de reservas...");
        return reservas;
    }

    public void calificar(Reserva reserva, int puntuacion, String comentario) {
        System.out.println(nombre + " calificó la reserva con " + puntuacion);
        System.out.println("Comentario: " + comentario);
    }
}
