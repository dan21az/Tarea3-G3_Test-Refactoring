package com.example.Strategy.pagos;

public class TarjetaMetodoPago implements MetodoPago {

    private final String numeroTarjeta;

    public TarjetaMetodoPago(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    @Override
    public String getTipo() {
        return "Tarjeta";
    }

    @Override
    public String getDescripcion() {
        return "Pago con tarjeta: " + numeroTarjeta;
    }

    @Override
    public boolean pagar() {
        if (numeroTarjeta == null || numeroTarjeta.isBlank() || numeroTarjeta.equalsIgnoreCase("error") || numeroTarjeta.equals("9999")) {
            System.out.println("Pago rechazado: número de tarjeta inválido.");
            return false;
        }
        System.out.println("Procesando pago con tarjeta terminada en " + numeroTarjeta.substring(Math.max(0, numeroTarjeta.length() - 4)) + ".");
        return true;
    }
}
