package com.example.dominio.reservas;

import com.example.Strategy.pagos.MetodoPago;
import com.example.Strategy.pagos.TarjetaMetodoPago;

public record ParametrosReserva(
    boolean aplicarTarifaExtra,
    boolean aplicarDepositoSeguridad,
    MetodoPago metodoPago
) {
    public static ParametrosReserva defaults() {
        return new ParametrosReserva(false, false, new TarjetaMetodoPago("0000000000000000"));
    }
}
