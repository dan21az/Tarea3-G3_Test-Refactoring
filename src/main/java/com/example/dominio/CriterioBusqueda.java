package com.example.dominio;
import java.util.List;

public class CriterioBusqueda {

    private String ubicacion;
    private double precioMin;
    private double precioMax;
    private String tipoAlojamiento;
    private List<String> servicios;

    public CriterioBusqueda(String ubicacion,
                            double precioMin,
                            double precioMax,
                            String tipoAlojamiento,
                            List<String> servicios) {

        this.ubicacion = ubicacion;
        this.precioMin = precioMin;
        this.precioMax = precioMax;
        this.tipoAlojamiento = tipoAlojamiento;
        this.servicios = servicios;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public double getPrecioMin() {
        return precioMin;
    }

    public double getPrecioMax() {
        return precioMax;
    }

    public String getTipoAlojamiento() {
        return tipoAlojamiento;
    }

    public List<String> getServicios() {
        return servicios;
    }
}
