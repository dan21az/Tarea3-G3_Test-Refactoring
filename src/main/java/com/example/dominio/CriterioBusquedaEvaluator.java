package com.example.dominio;

import com.example.Composite.CompPropiedad;
import com.example.Composite.Propiedad;
import com.example.Composite.Unidad;

public class CriterioBusquedaEvaluator {

    public boolean coincide(Propiedad propiedad, CriterioBusqueda criterio) {
        if (criterio == null) {
            return true;
        }

        return coincideUbicacion(propiedad, criterio)
                && coincidePrecio(propiedad, criterio)
                && coincideTipo(propiedad, criterio)
                && coincideServicios(propiedad, criterio);
    }

    private boolean coincideUbicacion(Propiedad propiedad, CriterioBusqueda criterio) {
        return criterio.getUbicacion() == null || criterio.getUbicacion().isBlank()
                || (propiedad.getDireccion() != null && propiedad.getDireccion().toLowerCase().contains(criterio.getUbicacion().toLowerCase()));
    }

    private boolean coincidePrecio(Propiedad propiedad, CriterioBusqueda criterio) {
        if (criterio.getPrecioMin() > 0 || criterio.getPrecioMax() > 0) {
            double precioMinUnidad = Double.MAX_VALUE;
            for (CompPropiedad child : propiedad.getChildren()) {
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

    private boolean coincideTipo(Propiedad propiedad, CriterioBusqueda criterio) {
        return criterio.getTipoAlojamiento() == null || criterio.getTipoAlojamiento().isBlank()
                || tieneTipo(propiedad, criterio.getTipoAlojamiento());
    }

    private boolean coincideServicios(Propiedad propiedad, CriterioBusqueda criterio) {
        return criterio.getServicios() == null || criterio.getServicios().isEmpty()
                || propiedad.getServicios().stream().anyMatch(criterio.getServicios()::contains);
    }

    private boolean tieneTipo(Propiedad propiedad, String tipo) {
        for (CompPropiedad child : propiedad.getChildren()) {
            if (child instanceof Unidad unidad && unidad.getTipo().equalsIgnoreCase(tipo)) {
                return true;
            }
        }
        return false;
    }
}
