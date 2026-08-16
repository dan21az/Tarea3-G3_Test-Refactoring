package com.example;

public class ControladorFactory {
    public static PerfilControlador crear(String perfilSeleccionado) {
        switch (perfilSeleccionado.toLowerCase()) {
            case "1", "huesped", "h":
                return new HuespedController(
                        AppConfig.huesped,
                        AppConfig.anfitrion,
                        AppConfig.servicioReserva,
                        AppConfig.servicioResena
                );
            case "2", "anfitrion", "a":
                return new AnfitrionController(
                        AppConfig.anfitrion,
                        AppConfig.manejadorIncidentes
                );
            case "3", "moderador", "m":
                return new ModeradorController(
                        AppConfig.moderadorActivo,
                        AppConfig.manejadorIncidentes,
                        AppConfig.servicioResena,
                        AppConfig.reservaActiva
                );
            default:
                return null;
        }
    }
}

