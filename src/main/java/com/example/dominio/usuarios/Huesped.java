package com.example.dominio.usuarios;

import com.example.Composite.Propiedad;
import com.example.Composite.Unidad;
import com.example.Singleton.BaseDatosSingleton;
import com.example.dominio.CriterioBusqueda;
import com.example.dominio.incidentes.Incidente;
import com.example.dominio.reservas.Reserva;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Huesped extends Usuario {

    private List<Reserva> reservas;

    public Huesped(String id, String nombre, String correo, String password) {
        super(id, nombre, correo, password);
        this.reservas = new ArrayList<>();
    }

    public List<Propiedad> buscarPropiedad(CriterioBusqueda criterio) {
        System.out.println(nombre + " está buscando propiedades...");
        BaseDatosSingleton db = BaseDatosSingleton.getInstance();
        List<Propiedad> resultados = new ArrayList<>();

        for (Propiedad propiedad : db.getCatalogo()) {
            if (propiedad.coincide(criterio)) {
                resultados.add(propiedad);
            }
        }

        return resultados;
    }

    public Reserva realizarReserva(Unidad unidad, Date inicio, Date fin) {
        System.out.println(nombre + " está realizando una reserva.");

        Reserva reserva = new Reserva(inicio, fin, this, unidad);
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
