package com.example.Decorator;
import com.example.Composite.CompPropiedad;
public class TarifaSeguridad extends TarifaDecorator {

    public TarifaSeguridad(CompPropiedad propiedad) {
        super(propiedad);
    }

    @Override
    public double costo() {

        double total = propiedad.costo() + 25.0;

        System.out.println("Aplicando depósito de seguridad (+25)");

        return total;
    }
}
