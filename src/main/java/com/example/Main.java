package com.example;

import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        ContenedorDependencias appConfig = new ContenedorDependencias();
        appConfig.inicializar();

        boolean salir = false;
        while (!salir) {
            mostrarMensaje("\n=================================");
            mostrarMensaje("   SELECCIONE UN PERFIL         ");
            mostrarMensaje("=================================");
            mostrarMensaje("1. Húesped");
            mostrarMensaje("2. Anfitrión");
            mostrarMensaje("3. Moderador");
            mostrarMensaje("4. Salir");
            String perfilSeleccionado = leerTexto("Seleccione un perfil: ");

            ControladorFactory factory = new ControladorFactory(scanner, appConfig.repositorio, appConfig);
            Runnable controlador = factory.crearControlador(perfilSeleccionado);

            if (controlador != null) {
                controlador.run();
            } else if (perfilSeleccionado.toLowerCase().matches("4|salir|s|q")) {
                mostrarMensaje("\nGracias por usar HomeStay.");
                salir = true;
            } else {
                mostrarMensaje("\nPerfil no válido. Intente nuevamente.");
            }
        }
    }

    private static void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }

    private static String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine().trim();
    }
}
