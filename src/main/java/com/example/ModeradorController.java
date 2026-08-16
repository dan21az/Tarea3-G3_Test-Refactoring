package com.example;

import com.example.ChainOfResponsibility.ManejadorIncidente;
import com.example.ChainOfResponsibility.ModeradorHandler;
import com.example.Singleton.Repositorio;
import com.example.dominio.incidentes.Incidente;
import com.example.dominio.resenas.ServicioResena;
import com.example.dominio.reservas.Reserva;
import com.example.dominio.usuarios.Moderador;

import java.util.List;
import java.util.Scanner;

public class ModeradorController {

    private final Repositorio db;
    private final ServicioResena servicioResena;
    private final ManejadorIncidente manejadorIncidentes;
    private final Moderador moderador;
    private Reserva reservaActiva;
    private final Scanner scanner;

    public ModeradorController(Moderador moderador, ManejadorIncidente manejadorIncidentes, ServicioResena servicioResena, Reserva reservaActiva, Scanner scanner, Repositorio db) {
        this.moderador = moderador;
        this.manejadorIncidentes = manejadorIncidentes;
        this.servicioResena = servicioResena;
        this.reservaActiva = reservaActiva;
        this.db = db;
        this.scanner = scanner;
    }

    public void ejecutar() {
        boolean volver = false;
        while (!volver) {
            mostrarMensaje("\n=== MENÚ MODERADOR ===");
            mostrarMensaje("1. Revisar incidente");
            mostrarMensaje("2. Ver estado del sistema");
            mostrarMensaje("3. Volver al menú de perfiles");
            String opcion = leerTexto("Seleccione una opción: ");

            switch (opcion.toLowerCase()) {
                case "1", "incidente", "revisar" -> revisarIncidente();
                case "2", "estado", "sistema" -> verEstadoSistema();
                case "3", "volver", "v" -> volver = true;
                default -> mostrarMensaje("\nOpción no válida.");
            }
        }
    }

    private void revisarIncidente() {
        mostrarMensaje("\n--- Moderador revisa incidente ---");
        List<Incidente> incidentes = db.getIncidentes();
        if (incidentes.isEmpty()) {
            mostrarMensaje("No hay incidentes registrados.");
            return;
        }

        mostrarIncidentes(incidentes);

        String idIncidente = leerTexto("Ingrese el ID del incidente a revisar: ");
        Incidente incidente = incidentes.stream()
                .filter(i -> i.getIdIncidente().equalsIgnoreCase(idIncidente))
                .findFirst()
                .orElse(null);
        if (incidente == null) {
            mostrarMensaje("Incidente no encontrado.");
            return;
        }

        procesarDecisionIncidente(incidente);
    }

    private void mostrarIncidentes(List<Incidente> incidentes) {
        for (Incidente incidente : incidentes) {
            mostrarMensaje("- " + incidente.getIdIncidente() + " | " + incidente.getDescripcion() + " | Estado: " + incidente.getEstado());
        }
    }

    private void procesarDecisionIncidente(Incidente incidente) {
        String decision = leerTexto("¿El moderador resuelve este incidente? (s/n): ");
        ManejadorIncidente moderadorHandler = manejadorIncidentes.buscarSiguiente(ModeradorHandler.class);
        if (moderadorHandler != null) {
            moderadorHandler.manejar(incidente, decision.equalsIgnoreCase("s"));
        } else {
            mostrarMensaje("No se encontró el manejador de moderador en la cadena.");
        }
    }

    private void verEstadoSistema() {
        mostrarMensaje("\n--- Estado del sistema ---");
        mostrarMensaje("Reserva activa: " + (reservaActiva != null ? "Sí" : "No"));
        mostrarMensaje("Propiedades registradas: " + db.getCatalogo().size());
        mostrarMensaje("Total reseñas: " + servicioResena.getResenas().size());
    }

    private void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }

    private String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine().trim();
    }
}
