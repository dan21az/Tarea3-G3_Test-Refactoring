package com.example.dominio.usuarios;
import com.example.dominio.incidentes.Incidente;
public abstract class PersonalSoporte {

    protected String nombre;

    public PersonalSoporte(String nombre) {
        this.nombre = nombre;
    }

    public abstract void atenderIncidente(Incidente incidente);

}