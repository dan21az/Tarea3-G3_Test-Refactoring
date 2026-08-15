package com.example.dominio.usuarios;

import java.util.ArrayList;
import java.util.List;

import com.example.Composite.CompPropiedad;
import com.example.Composite.Propiedad;
import com.example.Composite.Unidad;
import com.example.Singleton.BaseDatosSingleton;
import com.example.State.EstadoUnidad;
import com.example.dominio.incidentes.Incidente;
import com.example.dominio.reservas.Reserva;

public class Anfitrion extends Usuario {

    private List<Propiedad> propiedades;

    public Anfitrion(String id, String nombre, String correo, String password) {
        super(id, nombre, correo, password);
        propiedades = new ArrayList<>();
    }

    public void registrarPropiedad(Propiedad propiedad) {
        if (propiedad == null || !propiedad.esValida()) {
            System.out.println("No se puede registrar una propiedad sin unidades.");
            return;
        }

        propiedades.add(propiedad);
        BaseDatosSingleton.getInstance().guardarPropiedad(propiedad);
        System.out.println(nombre + " registró una nueva propiedad.");
    }

    public List<Propiedad> getPropiedades() {
        return propiedades;
    }

    public Propiedad registrarNuevaPropiedadConUnidades(String nombre, String direccion, String reglas,
                            List<CompPropiedad> unidades, List<String> servicios) {
        if (unidades == null || unidades.isEmpty()) {
            System.out.println("La propiedad no puede registrarse porque debe incluir al menos una unidad.");
            return null;
        }

        Propiedad nuevaPropiedad = new Propiedad(nombre, direccion, reglas);

        if (servicios != null) {
            for (String servicio : servicios) {
                nuevaPropiedad.añadirServicio(servicio);
            }
        }

        for (CompPropiedad unidad : unidades) {
            if (unidad != null) {
                nuevaPropiedad.añadirUnidad(unidad);
            }
        }

        if (!nuevaPropiedad.esValida()) {
            System.out.println("La propiedad no puede registrarse porque debe incluir al menos una unidad.");
            return null;
        }

        registrarPropiedad(nuevaPropiedad);
        return nuevaPropiedad;
    }

    public Propiedad buscarPropiedadPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return null;
        }

        for (Propiedad propiedad : propiedades) {
            if (propiedad.getNombre().equalsIgnoreCase(nombre.trim())) {
                return propiedad;
            }
        }

        return null;
    }

    public Unidad buscarUnidadEnPropiedad(String nombrePropiedad, String idUnidad) {
        Propiedad propiedadBuscada = buscarPropiedadPorNombre(nombrePropiedad);
        if (propiedadBuscada == null) {
            return null;
        }
        return propiedadBuscada.obtenerUnidadPorId(idUnidad);
    }

    public void actualizarHorarioDePropiedad(Propiedad propiedad, String checkIn, String checkOut) {
        if (propiedad == null) {
            return;
        }
        propiedad.actualizarHorarios(checkIn, checkOut);
    }

    public void gestionarReglas(Propiedad propiedad, String reglas) {
        propiedad.setReglas(reglas);

        System.out.println("Reglas actualizadas.");
    }

    public void actualizarEstadoUnidad(Unidad unidad, EstadoUnidad estado) {
        if (unidad == null || estado == null) {
            return;
        }

        unidad.cambiarEstado(estado);

        System.out.println("Estado de unidad actualizado.");
    }

    public List<Incidente> revisarIncidentesDeSusPropiedades() {
        List<Incidente> resultado = new ArrayList<>();
        BaseDatosSingleton db = BaseDatosSingleton.getInstance();

        for (Propiedad propiedad : propiedades) {
            resultado.addAll(db.buscarIncidentesPorPropiedad(propiedad));
        }

        return resultado;
    }

    public void resolverIncidente(Incidente incidente) {
        System.out.println(nombre + " intenta resolver incidente.");
        incidente.resolver();
    }

    public void calificar(Reserva reserva, int puntuacion, String comentario) {
        System.out.println(nombre + " calificó al huésped.");
        System.out.println("Comentario: " + comentario);
    }
}