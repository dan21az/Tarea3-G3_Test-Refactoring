package com.example.FactoryMethod;

import com.example.Composite.CompPropiedad;
import com.example.Composite.Unidad;

public class HabitacionPrivadaFactory extends UnidadFactory {
    @Override
    public CompPropiedad crearUnidad(String idUnidad, double precio) {
        return new Unidad(idUnidad, "Habitación Privada", precio);
    }
}
