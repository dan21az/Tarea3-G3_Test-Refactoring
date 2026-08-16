package com.example;

public class ContenedorDependencias {
    private final HuespedController huespedController;
    private final AnfitrionController anfitrionController;
    private final ModeradorController moderadorController;

    public ContenedorDependencias() {
        // Usamos los objetos inicializados en AppConfig
        this.huespedController = new HuespedController(
                AppConfig.huesped,
                AppConfig.anfitrion,
                AppConfig.servicioReserva,
                AppConfig.servicioResena
        );

        this.anfitrionController = new AnfitrionController(
                AppConfig.anfitrion,
                AppConfig.manejadorIncidentes
        );

        this.moderadorController = new ModeradorController(
                AppConfig.moderadorActivo,
                AppConfig.manejadorIncidentes,
                AppConfig.servicioResena,
                AppConfig.reservaActiva
        );
    }

    public HuespedController getHuespedController() { return huespedController; }
    public AnfitrionController getAnfitrionController() { return anfitrionController; }
    public ModeradorController getModeradorController() { return moderadorController; }
}
