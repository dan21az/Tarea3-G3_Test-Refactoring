package com.example.Singleton;
import java.util.ArrayList;
import java.util.List;

import com.example.Composite.CompPropiedad;
import com.example.Composite.Propiedad;
import com.example.dominio.incidentes.Incidente;
import com.example.dominio.reservas.Reserva;

public class BaseDatosSingleton implements Repositorio {

    private List<Propiedad> catalogo;
    private List<Reserva> reservas;
    private List<Incidente> incidentes;

    private BaseDatosSingleton() {
        catalogo = new ArrayList<>();
        reservas = new ArrayList<>();
        incidentes = new ArrayList<>();
    }

    private static class SingletonHolder {
        private static final BaseDatosSingleton INSTANCE = new BaseDatosSingleton();
    }

    public static BaseDatosSingleton getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void limpiar() {
        catalogo.clear();
        reservas.clear();
        incidentes.clear();
    }

    public void guardarPropiedad(Propiedad propiedad) {
        if (propiedad == null) {
            return;
        }

        boolean yaExiste = catalogo.stream()
                .anyMatch(p -> p == propiedad
                        || (p.getNombre() != null && p.getNombre().equalsIgnoreCase(propiedad.getNombre())
                        && p.getDireccion() != null && p.getDireccion().equalsIgnoreCase(propiedad.getDireccion())));

        if (!yaExiste) {
            catalogo.add(propiedad);
            System.out.println("Propiedad guardada en BD.");
        }
    }

    public void guardarReserva(Reserva reserva) {

        reservas.add(reserva);

        System.out.println("Reserva guardada en BD.");
    }

    public void guardarIncidente(Incidente incidente) {
        incidentes.add(incidente);
        System.out.println("Incidente guardado en BD: " + incidente.getIdIncidente());
    }

    public List<Propiedad> getCatalogo() {
        return catalogo;
    }

    public List<Reserva> getReservas() {
        return reservas;
    }

    public List<Incidente> getIncidentes() {
        return incidentes;
    }

    public Propiedad buscarPropiedadPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return null;
        }
        for (Propiedad propiedad : catalogo) {
            if (propiedad.getNombre().equalsIgnoreCase(nombre.trim())) {
                return propiedad;
            }
        }
        return null;
    }

    public List<Incidente> buscarIncidentesPorPropiedad(Propiedad propiedad) {
        List<Incidente> resultado = new ArrayList<>();
        if (propiedad == null || propiedad.getChildren() == null) {
            return resultado;
        }
        for (Incidente incidente : incidentes) {
            if (incidente == null || incidente.getReserva() == null || incidente.getReserva().getUnidad() == null) {
                continue;
            }
            for (CompPropiedad child : propiedad.getChildren()) {
                if (child == incidente.getReserva().getUnidad()) {
                    resultado.add(incidente);
                    break;
                }
            }
        }
        return resultado;
    }
}
