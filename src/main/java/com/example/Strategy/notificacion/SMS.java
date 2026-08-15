package com.example.Strategy.notificacion;
public class SMS implements Canal {

    @Override
    public void enviar(String mensaje) {
        System.out.println("Enviando SMS: " + mensaje);
    }
}
