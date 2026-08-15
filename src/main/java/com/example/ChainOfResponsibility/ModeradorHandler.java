package com.example.ChainOfResponsibility;

import com.example.dominio.incidentes.EstadoIncidente;
import com.example.dominio.incidentes.Incidente;
import com.example.dominio.usuarios.Moderador;

public class ModeradorHandler extends ManejadorIncidente {

    private Moderador asignado;

    public ModeradorHandler(Moderador asignado) {
        this.asignado = asignado;
    }

    @Override
    public void manejar(Incidente incidente) {
        manejar(incidente, false);
    }

    @Override
    public String getRol() {
        return "Moderador";
    }

    @Override
    public void resolverIncidente(Incidente incidente) {
        incidente.resolverConRevision();
    }

    @Override
    public void noResolverIncidente(Incidente incidente) {
        incidente.cambiarEstado(EstadoIncidente.CRITICO);
    }
}
