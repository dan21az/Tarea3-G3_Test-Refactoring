package com.example;

import java.util.Scanner;
import com.example.Singleton.Repositorio;

public class ControladorFactory {

    private final Scanner scanner;
    private final Repositorio repositorio;
    private final ContenedorDependencias appConfig;

    public ControladorFactory(Scanner scanner, Repositorio repositorio, ContenedorDependencias appConfig) {
        this.scanner = scanner;
        this.repositorio = repositorio;
        this.appConfig = appConfig;
    }

    public Runnable crearControlador(String perfilSeleccionado) {
        return switch (perfilSeleccionado.toLowerCase()) {
            case "1", "huesped", "h" -> () -> new HuespedController(appConfig.huesped, appConfig.anfitrion, appConfig.servicioReserva, appConfig.servicioResena, scanner, repositorio).ejecutar();
            case "2", "anfitrion", "a" -> () -> new AnfitrionController(appConfig.anfitrion, appConfig.manejadorIncidentes, scanner, repositorio).ejecutar();
            case "3", "moderador", "m" -> () -> new ModeradorController(appConfig.moderadorActivo, appConfig.manejadorIncidentes, appConfig.servicioResena, null, scanner, repositorio).ejecutar();
            default -> null;
        };
    }
}
