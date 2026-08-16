package com.example;

import com.example.Singleton.Repositorio;
import com.example.dominio.resenas.ServicioResena;
import com.example.dominio.reservas.ServicioReserva;
import com.example.dominio.usuarios.Anfitrion;
import com.example.dominio.usuarios.Huesped;

import java.util.Scanner;

public class HuespedController {

    private final HuespedBusquedaUI busquedaUI;
    private final HuespedReservaUI reservaUI;
    private final HuespedInteraccionesUI interaccionesUI;
    private final Scanner scanner;

    public HuespedController(Huesped huesped, Anfitrion anfitrion, ServicioReserva servicioReserva, ServicioResena servicioResena, Scanner scanner, Repositorio repositorio) {
        this.scanner = scanner;
        this.busquedaUI = new HuespedBusquedaUI(huesped, repositorio, scanner);
        this.reservaUI = new HuespedReservaUI(huesped, repositorio, servicioReserva, scanner, busquedaUI);
        this.interaccionesUI = new HuespedInteraccionesUI(huesped, repositorio, servicioResena, scanner, reservaUI);
    }

    public void ejecutar() {
        boolean volver = false;
        while (!volver) {
            mostrarMensaje("\n=== MENÚ HUÉSPED ===");
            mostrarMensaje("1. Buscar propiedades por criterios (ubicación, precio, tipo, servicios)");
            mostrarMensaje("2. Ver todas las propiedades");
            mostrarMensaje("3. Realizar reserva");
            mostrarMensaje("4. Ver / Cancelar reserva activa");
            mostrarMensaje("5. Reportar incidente");
            mostrarMensaje("6. Crear reseña");
            mostrarMensaje("7. Volver al menú de perfiles");
            String opcion = leerTexto("Seleccione una opción: ");

            switch (opcion.toLowerCase()) {
                case "1", "buscar" -> busquedaUI.buscarPropiedadesPorCriterios();
                case "2", "todas", "ver" -> busquedaUI.mostrarCatalogoCompleto();
                case "3", "reservar", "reserva" -> reservaUI.realizarReserva();
                case "4", "cancelar", "ver reserva" -> reservaUI.verOCancelarReserva();
                case "5", "incidente" -> interaccionesUI.gestionarIncidente();
                case "6", "reseña", "resena" -> interaccionesUI.crearResena();
                case "7", "volver", "v" -> volver = true;
                default -> mostrarMensaje("\nOpción no válida.");
            }
        }
    }

    private void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }

    private String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine().trim();
    }
}
