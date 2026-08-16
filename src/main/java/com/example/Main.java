package com.example;

public class Main {

    public static void main(String[] args) {
        AppConfig.inicializarDatosPrueba();
        ContenedorDependencias contenedor = new ContenedorDependencias();

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
                case "1", "huesped", "h" -> contenedor.getHuespedController().ejecutar();
                case "2", "anfitrion", "a" -> contenedor.getAnfitrionController().ejecutar();
                case "3", "moderador", "m" -> contenedor.getModeradorController().ejecutar();
                case "4", "salir", "s", "q" -> {
                    ConsoleUI.mostrarMensaje("\nGracias por usar HomeStay.");
                    salir = true;
                }
                default -> ConsoleUI.mostrarMensaje("\nPerfil no válido. Intente nuevamente.");
            }
        }
    }
}
