package com.example.Strategy.pagos;

public class PayPalMetodoPago implements MetodoPago {

    private final String correo;

    public PayPalMetodoPago(String correo) {
        this.correo = correo;
    }

    @Override
    public String getTipo() {
        return "PayPal";
    }

    @Override
    public String getDescripcion() {
        return "Pago con PayPal: " + correo;
    }

    @Override
    public boolean pagar() {
        if (correo == null || correo.isBlank() || correo.equalsIgnoreCase("error")) {
            System.out.println("Pago rechazado: correo de PayPal inválido.");
            return false;
        }
        System.out.println("Procesando pago con PayPal para: " + correo);
        return true;
    }
}
