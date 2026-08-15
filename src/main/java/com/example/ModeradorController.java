package com.example;

import com.example.ChainOfResponsibility.ManejadorIncidente;
import com.example.ChainOfResponsibility.ModeradorHandler;
import com.example.Singleton.BaseDatosSingleton;
import com.example.dominio.incidentes.Incidente;
import com.example.dominio.resenas.ServicioResena;
import com.example.dominio.reservas.Reserva;
import com.example.dominio.usuarios.Moderador;

import java.util.List;

public class ModeradorController {

    private final BaseDatosSingleton db;
    private final ServicioResena servicioResena;
    private final ManejadorIncidente manejadorIncidentes;
    private final Moderador moderador;
    private Reserva reservaActiva;

    public ModeradorController(Moderador moderador, ManejadorIncidente manejadorIncidentes, ServicioResena servicioResena, Reserva reservaActiva) {
        this.moderador = moderador;
        this.manejadorIncidentes = manejadorIncidentes;
        this.servicioResena = servicioResena;
        this.reservaActiva = reservaActiva;
        this.db = BaseDatosSingleton.getInstance();
    }

    public void ejecutar() {
        boolean volver = false;
        while (!volver) {
            ConsoleUI.mostrarMensaje("\n=== MENÚ MODERADOR ===");
            ConsoleUI.mostrarMensaje("1. Revisar incidente");
            ConsoleUI.mostrarMensaje("2. Ver estado del sistema");
            ConsoleUI.mostrarMensaje("3. Volver al menú de perfiles");
            String opcion = ConsoleUI.leerTexto("Seleccione una opción: ");

            switch (opcion.toLowerCase()) {
                case "1", "incidente", "revisar" -> revisarIncidente();
                case "2", "estado", "sistema" -> verEstadoSistema();
                case "3", "volver", "v" -> volver = true;
                default -> ConsoleUI.mostrarMensaje("\nOpción no válida.");
            }
        }
    }

    private void revisarIncidente() {
        ConsoleUI.mostrarMensaje("\n--- Moderador revisa incidente ---");
        List<Incidente> incidentes = db.getIncidentes();
        if (incidentes.isEmpty()) {
            ConsoleUI.mostrarMensaje("No hay incidentes registrados.");
            return;
        }

        mostrarIncidentes(incidentes);

        String idIncidente = ConsoleUI.leerTexto("Ingrese el ID del incidente a revisar: ");
        Incidente incidente = incidentes.stream()
                .filter(i -> i.getIdIncidente().equalsIgnoreCase(idIncidente))
                .findFirst()
                .orElse(null);
        if (incidente == null) {
            ConsoleUI.mostrarMensaje("Incidente no encontrado.");
            return;
        }

        procesarDecisionIncidente(incidente);
    }

    private void mostrarIncidentes(List<Incidente> incidentes) {
        for (Incidente incidente : incidentes) {
            ConsoleUI.mostrarMensaje("- " + incidente.getIdIncidente() + " | " + incidente.getDescripcion() + " | Estado: " + incidente.getEstado());
        }
    }

    private void procesarDecisionIncidente(Incidente incidente) {
        String decision = ConsoleUI.leerTexto("¿El moderador resuelve este incidente? (s/n): ");
        ManejadorIncidente moderadorHandler = manejadorIncidentes.buscarSiguiente(ModeradorHandler.class);
        if (moderadorHandler != null) {
            moderadorHandler.manejar(incidente, decision.equalsIgnoreCase("s"));
        } else {
            ConsoleUI.mostrarMensaje("No se encontró el manejador de moderador en la cadena.");
        }
    }

    private void verEstadoSistema() {
        ConsoleUI.mostrarMensaje("\n--- Estado del sistema ---");
        ConsoleUI.mostrarMensaje("Reserva activa: " + (reservaActiva != null ? "Sí" : "No"));
        ConsoleUI.mostrarMensaje("Propiedades registradas: " + db.getCatalogo().size());
        ConsoleUI.mostrarMensaje("Total reseñas: " + servicioResena.getResenas().size());
    }
}
