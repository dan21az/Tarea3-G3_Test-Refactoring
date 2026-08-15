package com.example.Decorator;
import com.example.Composite.CompPropiedad;
public abstract class TarifaDecorator implements CompPropiedad {

    protected CompPropiedad propiedad;

    public TarifaDecorator(CompPropiedad propiedad) {
        this.propiedad = propiedad;
    }

    @Override
    public abstract double costo();
}