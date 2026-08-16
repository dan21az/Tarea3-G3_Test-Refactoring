package com.example;

import com.example.Singleton.Repositorio;
import com.example.dominio.incidentes.Incidente;
import com.example.dominio.resenas.ServicioResena;
import com.example.dominio.reservas.Reserva;
import com.example.dominio.usuarios.Huesped;

import java.util.Scanner;

public class HuespedInteraccionesUI {

    private final Huesped huesped;
    private final Repositorio repositorio;
    private final ServicioResena servicioResena;
    private final Scanner scanner;
    private final HuespedReservaUI reservaUI;

    public HuespedInteraccionesUI(Huesped huesped, Repositorio repositorio, ServicioResena servicioResena, Scanner scanner, HuespedReservaUI reservaUI) {
        this.huesped = huesped;
        this.repositorio = repositorio;
        this.servicioResena = servicioResena;
        this.scanner = scanner;
        this.reservaUI = reservaUI;
    }

    public void gestionarIncidente() {
        mostrarMensaje("\n--- Gestión de Incidentes ---");
        Reserva reservaSeleccionada = reservaUI.seleccionarReservaDelHuesped();
        if (reservaSeleccionada == null) {
            return;
        }

        String descripcion = leerTexto("Descripción del problema: ");
        Incidente incidente = huesped.reportarIncidente(descripcion, reservaSeleccionada);
        repositorio.guardarIncidente(incidente);

        mostrarMensaje("ID del incidente generado: " + incidente.getIdIncidente());
        mostrarMensaje("\nIncidente registrado. El anfitrión lo revisará.");
    }

    public void crearResena() {
        mostrarMensaje("\n--- Crear Reseña ---");
        Reserva reservaSeleccionada = reservaUI.seleccionarReservaDelHuesped();
        if (reservaSeleccionada == null) {
            return;
        }

        String comentario = leerTexto("Comentario: ");
        int calificacion = leerInt("Calificación (1-5): ");

        servicioResena.crearResena(huesped, comentario, calificacion, reservaSeleccionada);
        mostrarMensaje("Reseña creada con éxito. Total de reseñas registradas: " + servicioResena.getResenas().size());
    }

    private void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }

    private String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine().trim();
    }

    private int leerInt(String mensaje) {
        while (true) {
            try {
                return Integer.parseInt(leerTexto(mensaje));
            } catch (NumberFormatException e) {
                System.out.println("Error: Ingrese un número entero válido.");
            }
        }
    }
}
