package com.example.Singleton;

import java.util.List;

import com.example.Composite.Propiedad;
import com.example.dominio.incidentes.Incidente;
import com.example.dominio.reservas.Reserva;

public interface Repositorio {

    void limpiar();

    void guardarPropiedad(Propiedad propiedad);

    void guardarReserva(Reserva reserva);

    void guardarIncidente(Incidente incidente);

    List<Propiedad> getCatalogo();

    List<Reserva> getReservas();

    List<Incidente> getIncidentes();

    Propiedad buscarPropiedadPorNombre(String nombre);

    List<Incidente> buscarIncidentesPorPropiedad(Propiedad propiedad);

    Propiedad buscarPropiedadPorUnidad(com.example.Composite.Unidad unidad);
}
