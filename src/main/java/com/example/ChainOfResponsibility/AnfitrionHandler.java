package com.example.ChainOfResponsibility;

import com.example.dominio.incidentes.Incidente;
import com.example.dominio.usuarios.Anfitrion;

public class AnfitrionHandler extends ManejadorIncidente {

    private Anfitrion asignado;

    public AnfitrionHandler(Anfitrion asignado) {
        this.asignado = asignado;
    }

    @Override
    public void manejar(Incidente incidente) {
        manejar(incidente, false);
    }

    @Override
    public String getRol() {
        return "Anfitrión";
    }

    @Override
    public void resolverIncidente(Incidente incidente) {
        incidente.resolver();
    }

    @Override
    public void noResolverIncidente(Incidente incidente) {
        incidente.escalar();
    }
}
