package com.example.dominio.usuarios;
import com.example.dominio.incidentes.Incidente;
public class SoporteLegal extends PersonalSoporte {

    public SoporteLegal(String nombre) {
        super(nombre);
    }

    @Override
    public void atenderIncidente(Incidente incidente) {
        System.out.println("Soporte legal " + nombre + " resolviendo incidente crítico.");
    }
}