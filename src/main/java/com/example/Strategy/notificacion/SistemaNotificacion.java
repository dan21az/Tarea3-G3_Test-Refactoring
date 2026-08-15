package com.example.Strategy.notificacion;
import com.example.dominio.usuarios.Usuario;
public class SistemaNotificacion {

    private Canal canal;

    public SistemaNotificacion(Canal canal) {
        this.canal = canal;
    }

    // cambiar estrategia dinámicamente
    public void setCanal(Canal canal) {
        this.canal = canal;
    }

    public void enviarMensaje(Usuario usuario, String mensaje) {

        System.out.println("Preparando notificación para: "
                + usuario.getNombre());

        canal.enviar(mensaje);
    }
}
