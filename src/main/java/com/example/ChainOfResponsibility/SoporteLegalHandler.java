package com.example.ChainOfResponsibility;

import com.example.dominio.incidentes.EstadoIncidente;
import com.example.dominio.incidentes.Incidente;
import com.example.dominio.usuarios.SoporteLegal;

public class SoporteLegalHandler extends ManejadorIncidente {

    private SoporteLegal asignado;

    public SoporteLegalHandler(SoporteLegal asignado) {
        this.asignado = asignado;
    }

    @Override
    public void manejar(Incidente incidente) {
        manejar(incidente, false);
    }

    @Override
    public String getRol() {
        return "Soporte legal";
    }

    @Override
    public String getMensajeNoResuelve(Incidente incidente) {
        return "Soporte legal no resolvió el incidente y queda pendiente de revisión.";
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
