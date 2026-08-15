package com.example.State;
import com.example.Composite.Unidad;
public class Mantenimiento implements EstadoUnidad {

    @Override
    public void ocupar(Unidad unidad) {
        System.out.println("No se puede reservar, unidad en mantenimiento.");
    }

    @Override
    public void liberar(Unidad unidad) {
        System.out.println("Mantenimiento finalizado.");

        unidad.cambiarEstado(new Disponible());
    }
}
