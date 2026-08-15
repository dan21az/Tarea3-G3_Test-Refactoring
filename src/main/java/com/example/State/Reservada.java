package com.example.State;
import com.example.Composite.Unidad;
public class Reservada implements EstadoUnidad {

    @Override
    public void ocupar(Unidad unidad) {
        System.out.println("La unidad pasa a estado ocupada.");

        unidad.cambiarEstado(new Ocupada());
    }

    @Override
    public void liberar(Unidad unidad) {
        System.out.println("Reserva cancelada. Volviendo a disponible.");

        unidad.cambiarEstado(new Disponible());
    }
}
