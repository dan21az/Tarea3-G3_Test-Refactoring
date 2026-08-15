package com.example.FactoryMethod;

import com.example.Composite.CompPropiedad;
import com.example.Composite.Unidad;

public class DepartamentoFactory extends UnidadFactory {
    @Override
    public CompPropiedad crearUnidad(String idUnidad, double precio) {
        return new Unidad(idUnidad, "Departamento Completo", precio);
    }
}
