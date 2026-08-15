package com.example.ChainOfResponsibility;

import com.example.dominio.incidentes.Incidente;
import com.example.dominio.incidentes.ServicioIncidente;

public abstract class ManejadorIncidente {

    protected ManejadorIncidente siguiente;
    private final ServicioIncidente servicioIncidente;

    protected ManejadorIncidente() {
        this(new ServicioIncidente());
    }

    protected ManejadorIncidente(ServicioIncidente servicioIncidente) {
        this.servicioIncidente = servicioIncidente;
    }

    public void setSiguiente(ManejadorIncidente siguiente) {
        this.siguiente = siguiente;
    }

    public ManejadorIncidente getSiguiente() {
        return siguiente;
    }

    public <T extends ManejadorIncidente> T buscarSiguiente(Class<T> tipo) {
        ManejadorIncidente actual = siguiente;
        while (actual != null) {
            if (tipo.isInstance(actual)) {
                return tipo.cast(actual);
            }
            actual = actual.siguiente;
        }
        return null;
    }

    public void manejar(Incidente incidente, boolean resuelto) {
        servicioIncidente.manejar(this, incidente, resuelto);
    }

    public abstract void manejar(Incidente incidente);

    public abstract String getRol();

    public abstract void resolverIncidente(Incidente incidente);

    public abstract void noResolverIncidente(Incidente incidente);

    public String getMensajeNoResuelve(Incidente incidente) {
        return getRol() + " no puede resolver el incidente.";
    }
}
