package com.example.Composite;

import java.util.ArrayList;
import java.util.List;

import com.example.dominio.CriterioBusqueda;
import com.example.dominio.reservas.PoliticaCancelacion;

public class Propiedad implements CompPropiedad {

    private String nombre;
    private String direccion;
    private String reglas;
    private String checkIn;
    private String checkOut;
    private List<String> servicios;
    private List<String> restricciones;
    private List<CompPropiedad> children;
    private PoliticaCancelacion politicaCancelacion;

    public Propiedad(String nombre, String direccion, String reglas) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.reglas = reglas;
        this.servicios = new ArrayList<>();
        this.restricciones = new ArrayList<>();
        this.children = new ArrayList<>();
    }

    public void añadirUnidad(CompPropiedad unidad) {
        children.add(unidad);
        if (unidad instanceof Unidad u) {
            u.setPropiedad(this);
        }
        System.out.println("Unidad añadida a propiedad " + nombre);
    }

    public void removerUnidad(CompPropiedad unidad) {
        children.remove(unidad);
        System.out.println("Unidad eliminada.");
    }

    public List<CompPropiedad> getChildren() {
        return children;
    }

    public void setReglas(String reglas) {
        this.reglas = reglas;
    }

    public String getReglas() {
        return reglas;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setCheckIn(String checkIn) {
        this.checkIn = checkIn;
    }

    public void setCheckOut(String checkOut) {
        this.checkOut = checkOut;
    }

    public String getCheckIn() {
        return checkIn;
    }

    public String getCheckOut() {
        return checkOut;
    }

    public void añadirServicio(String servicio) {
        servicios.add(servicio);
    }

    public List<String> getServicios() {
        return servicios;
    }

    public void añadirRestriccion(String restriccion) {
        restricciones.add(restriccion);
    }

    public List<String> getRestricciones() {
        return restricciones;
    }

    public boolean tieneUnidadConId(String idUnidad) {
        return obtenerUnidadPorId(idUnidad) != null;
    }

    public Unidad obtenerUnidadPorId(String idUnidad) {
        if (idUnidad == null || idUnidad.isBlank()) {
            return null;
        }

        for (CompPropiedad child : children) {
            if (child instanceof Unidad unidad && unidad.getIdUnidad().equalsIgnoreCase(idUnidad.trim())) {
                return unidad;
            }
        }

        return null;
    }

    public void actualizarHorarios(String nuevoCheckIn, String nuevoCheckOut) {
        if (nuevoCheckIn != null && !nuevoCheckIn.isBlank()) {
            this.checkIn = nuevoCheckIn;
        }
        if (nuevoCheckOut != null && !nuevoCheckOut.isBlank()) {
            this.checkOut = nuevoCheckOut;
        }
    }

    public boolean coincide(CriterioBusqueda criterio) {
        if (criterio == null) {
            return true;
        }

        return coincideUbicacion(criterio)
                && coincidePrecio(criterio)
                && coincideTipo(criterio)
                && coincideServicios(criterio);
    }

    private boolean coincideUbicacion(CriterioBusqueda criterio) {
        return criterio.getUbicacion() == null || criterio.getUbicacion().isBlank()
                || (direccion != null && direccion.toLowerCase().contains(criterio.getUbicacion().toLowerCase()));
    }

    private boolean coincidePrecio(CriterioBusqueda criterio) {
        if (criterio.getPrecioMin() > 0 || criterio.getPrecioMax() > 0) {
            double precioMinUnidad = Double.MAX_VALUE;
            for (CompPropiedad child : children) {
                if (child instanceof Unidad unidad) {
                    precioMinUnidad = Math.min(precioMinUnidad, unidad.getPrecio());
                }
            }
            if (precioMinUnidad == Double.MAX_VALUE) {
                return false;
            }
            return precioMinUnidad >= criterio.getPrecioMin()
                    && (criterio.getPrecioMax() <= 0 || precioMinUnidad <= criterio.getPrecioMax());
        }
        return true;
    }

    private boolean coincideTipo(CriterioBusqueda criterio) {
        return criterio.getTipoAlojamiento() == null || criterio.getTipoAlojamiento().isBlank()
                || tieneTipo(criterio.getTipoAlojamiento());
    }

    private boolean coincideServicios(CriterioBusqueda criterio) {
        return criterio.getServicios() == null || criterio.getServicios().isEmpty()
                || servicios.stream().anyMatch(criterio.getServicios()::contains);
    }

    private boolean tieneTipo(String tipo) {
        for (CompPropiedad child : children) {
            if (child instanceof Unidad unidad && unidad.getTipo().equalsIgnoreCase(tipo)) {
                return true;
            }
        }
        return false;
    }

    public boolean esValida() {
        return children != null && children.stream().anyMatch(c -> c instanceof Unidad);
    }

    public PoliticaCancelacion getPoliticaCancelacion() {
        return politicaCancelacion;
    }

    public void setPoliticaCancelacion(PoliticaCancelacion politicaCancelacion) {
        this.politicaCancelacion = politicaCancelacion;
    }

    @Override
    public double costo() {

        double total = 0;

        for (CompPropiedad c : children) {
            total += c.costo();
        }

        return total;
    }
}
