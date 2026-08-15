package com.example.State;
import com.example.Composite.Unidad;
public class Ocupada implements EstadoUnidad {

    @Override
    public void ocupar(Unidad unidad) {
        System.out.println("La unidad ya está ocupada.");
    }

    @Override
    public void liberar(Unidad unidad) {
        System.out.println("Check-out realizado.");

        unidad.cambiarEstado(new Disponible());
    }
}