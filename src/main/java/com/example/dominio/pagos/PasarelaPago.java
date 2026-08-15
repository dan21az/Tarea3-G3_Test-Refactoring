package com.example.dominio.pagos;

import com.example.Strategy.pagos.Pago;

public interface PasarelaPago {

    boolean procesarTransaccion(Pago pago);

}