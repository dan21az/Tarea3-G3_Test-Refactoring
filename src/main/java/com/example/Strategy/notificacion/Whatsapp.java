package com.example.Strategy.notificacion;
public class Whatsapp implements Canal {

    @Override
    public void enviar(String mensaje) {
        System.out.println("Enviando WHATSAPP: " + mensaje);
    }
}
