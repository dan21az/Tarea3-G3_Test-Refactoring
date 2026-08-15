package com.example.dominio.pagos;

import com.example.Strategy.pagos.Pago;

public class PasarelaAdapter implements PasarelaPago {

    private ServicioPasarelaExterno servicio;

    public PasarelaAdapter(ServicioPasarelaExterno servicio) {
        this.servicio = servicio;
    }

    @Override
    public boolean procesarTransaccion(Pago pago) {

        System.out.println("Adaptando solicitud al servicio externo...");

        return servicio.procesarPagoExterno(
                pago.getMonto()
        );
    }
}