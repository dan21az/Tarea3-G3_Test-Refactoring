package com.example;

public class Main {

    public static void main(String[] args) {
        AppConfig.inicializarDatosPrueba();

        boolean salir = false;
        while (!salir) {
            ConsoleUI.mostrarMensaje("\n=================================");
            ConsoleUI.mostrarMensaje("   SELECCIONE UN PERFIL         ");
            ConsoleUI.mostrarMensaje("=================================");
            ConsoleUI.mostrarMensaje("1. Húesped");
            ConsoleUI.mostrarMensaje("2. Anfitrión");
            ConsoleUI.mostrarMensaje("3. Moderador");
            ConsoleUI.mostrarMensaje("4. Salir");
            String perfilSeleccionado = ConsoleUI.leerTexto("Seleccione un perfil: ");

            switch (perfilSeleccionado.toLowerCase()) {
                case "1", "huesped", "h" -> new HuespedController(AppConfig.huesped, AppConfig.anfitrion, AppConfig.servicioReserva, AppConfig.servicioResena).ejecutar();
                case "2", "anfitrion", "a" -> new AnfitrionController(AppConfig.anfitrion, AppConfig.manejadorIncidentes).ejecutar();
                case "3", "moderador", "m" -> new ModeradorController(AppConfig.moderadorActivo, AppConfig.manejadorIncidentes, AppConfig.servicioResena, AppConfig.reservaActiva).ejecutar();
                case "4", "salir", "s", "q" -> {
                    ConsoleUI.mostrarMensaje("\nGracias por usar HomeStay.");
                    salir = true;
                }
                default -> ConsoleUI.mostrarMensaje("\nPerfil no válido. Intente nuevamente.");
            }
        }
    }
}
