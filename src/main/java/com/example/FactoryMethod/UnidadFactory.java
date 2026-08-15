package com.example.FactoryMethod;

import com.example.Composite.CompPropiedad;

public abstract class UnidadFactory {

    public abstract CompPropiedad crearUnidad(String idUnidad, double precio);
}
