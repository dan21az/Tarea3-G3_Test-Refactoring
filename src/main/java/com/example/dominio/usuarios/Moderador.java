package com.example.dominio.usuarios;
import com.example.dominio.incidentes.Incidente;
public class Moderador extends PersonalSoporte {

    public Moderador(String nombre) {
        super(nombre);
    }

    @Override
    public void atenderIncidente(Incidente incidente) {
        System.out.println("Moderador " + nombre + " está revisando incidente.");
    }
}