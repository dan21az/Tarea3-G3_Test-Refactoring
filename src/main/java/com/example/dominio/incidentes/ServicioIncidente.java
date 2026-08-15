package com.example.dominio.incidentes;

import com.example.ChainOfResponsibility.ManejadorIncidente;

public class ServicioIncidente {

    public void manejar(ManejadorIncidente manejador, Incidente incidente, boolean resuelto) {
        if (incidente.getEstado() == EstadoIncidente.RESUELTO) {
            System.out.println(mensajeYaResuelto(incidente, manejador.getRol()));
            return;
        }

        System.out.println(mensajeRevisando(incidente, manejador.getRol()));

        if (resuelto) {
            System.out.println(mensajeResolviendo(incidente, manejador.getRol()));
            manejador.resolverIncidente(incidente);
            return;
        }

        System.out.println(manejador.getMensajeNoResuelve(incidente));
        manejador.noResolverIncidente(incidente);

        EstadoIncidente estado = incidente.getEstado();
        if (estado != EstadoIncidente.RESUELTO && estado != EstadoIncidente.CRITICO) {
            ManejadorIncidente siguiente = manejador.getSiguiente();
            if (siguiente != null) {
                siguiente.manejar(incidente, false);
            }
        }
    }

    private String mensajeYaResuelto(Incidente incidente, String rol) {
        return "El incidente " + incidente.getIdIncidente() + " ya está resuelto. " + rol + " no puede modificarlo.";
    }

    private String mensajeRevisando(Incidente incidente, String rol) {
        return rol + " revisando incidente " + incidente.getIdIncidente() + "...";
    }

    private String mensajeResolviendo(Incidente incidente, String rol) {
        return rol + " resuelve el incidente.";
    }
}
