package com.example;

import com.example.Composite.CompPropiedad;
import com.example.Composite.Propiedad;
import com.example.Composite.Unidad;
import com.example.ChainOfResponsibility.ManejadorIncidente;
import com.example.FactoryMethod.UnidadFactory;
import com.example.State.Disponible;
import com.example.State.Mantenimiento;
import com.example.dominio.incidentes.Incidente;
import com.example.dominio.reservas.PoliticaCancelacion;
import com.example.dominio.usuarios.Anfitrion;

import java.util.ArrayList;
import java.util.List;

public class AnfitrionController implements PerfilControlador{

    private final Anfitrion anfitrion;
    private final ManejadorIncidente manejadorIncidentes;
    private Propiedad propiedad;

    public AnfitrionController(Anfitrion anfitrion, ManejadorIncidente manejadorIncidentes) {
        this.anfitrion = anfitrion;
        this.manejadorIncidentes = manejadorIncidentes;
    }
    @Override

    public void ejecutar() {
        boolean volver = false;
        while (!volver) {
            ConsoleUI.mostrarMensaje("\n=== MENÚ ANFITRIÓN ===");
            ConsoleUI.mostrarMensaje("1. Gestionar reglas y estado de unidad");
            ConsoleUI.mostrarMensaje("2. Revisar incidentes de mi propiedad");
            ConsoleUI.mostrarMensaje("3. Ver información de la propiedad");
            ConsoleUI.mostrarMensaje("4. Añadir nueva propiedad y unidades");
            ConsoleUI.mostrarMensaje("5. Volver al menú de perfiles");
            String opcion = ConsoleUI.leerTexto("Seleccione una opción: ");

            switch (opcion.toLowerCase()) {
                case "1", "reglas", "estado" -> gestionarReglasYEstado();
                case "2", "incidente", "incidentes" -> revisarIncidentesDelAnfitrion();
                case "3", "info" -> mostrarInformacionPropiedad();
                case "4", "nueva", "añadir" -> registrarNuevaPropiedad();
                case "5", "volver", "v" -> volver = true;
                default -> ConsoleUI.mostrarMensaje("\nOpción no válida.");
            }
        }
    }

    private void gestionarReglasYEstado() {
        Propiedad propiedadSeleccionada = seleccionarPropiedadDelAnfitrion();
        if (propiedadSeleccionada == null) {
            return;
        }

        propiedad = propiedadSeleccionada;
        Unidad unidadSeleccionada = seleccionarUnidadDePropiedad(propiedadSeleccionada);
        if (unidadSeleccionada == null) {
            ConsoleUI.mostrarMensaje("No hay unidades disponibles para gestionar en esa propiedad.");
            return;
        }

        String subOpcion = mostrarOpcionesGestion();

        ejecutarSubOpcion(subOpcion, unidadSeleccionada);
    }

    private String mostrarOpcionesGestion() {
        ConsoleUI.mostrarMensaje("\n--- Gestión de Reglas y Estado de Unidad ---");
        ConsoleUI.mostrarMensaje("1. Actualizar reglas de la propiedad");
        ConsoleUI.mostrarMensaje("2. Cambiar estado a Mantenimiento");
        ConsoleUI.mostrarMensaje("3. Cambiar estado a Disponible");
        ConsoleUI.mostrarMensaje("4. Actualizar horario de check-in / check-out");
        return ConsoleUI.leerTexto("Seleccione opción: ");
    }

    private void ejecutarSubOpcion(String subOpcion, Unidad unidadSeleccionada) {
        switch (subOpcion) {
            case "1" -> {
                String nuevaRegla = ConsoleUI.leerTexto("Ingrese nueva regla: ");
                anfitrion.gestionarReglas(propiedad, nuevaRegla);
                ConsoleUI.mostrarMensaje("Regla registrada exitosamente.");
            }
            case "2" -> {
                anfitrion.actualizarEstadoUnidad(unidadSeleccionada, new Mantenimiento());
                ConsoleUI.mostrarMensaje("Estado de unidad actualizado a: Mantenimiento");
            }
            case "3" -> {
                anfitrion.actualizarEstadoUnidad(unidadSeleccionada, new Disponible());
                ConsoleUI.mostrarMensaje("Estado de unidad actualizado a: Disponible");
            }
            case "4" -> actualizarHorariosPropiedad();
            default -> ConsoleUI.mostrarMensaje("Sub-opción no válida.");
        }
    }

    private void actualizarHorariosPropiedad() {
        String nuevoCheckIn = ConsoleUI.leerTexto("Ingrese el nuevo horario de check-in (HH:mm): ");
        String nuevoCheckOut = ConsoleUI.leerTexto("Ingrese el nuevo horario de check-out (HH:mm): ");
        anfitrion.actualizarHorarioDePropiedad(propiedad, nuevoCheckIn, nuevoCheckOut);
        ConsoleUI.mostrarMensaje("Horario de la propiedad actualizado.");
        ConsoleUI.mostrarMensaje("Check-in: " + propiedad.getCheckIn());
        ConsoleUI.mostrarMensaje("Check-out: " + propiedad.getCheckOut());
    }

    private void revisarIncidentesDelAnfitrion() {
        ConsoleUI.mostrarMensaje("\n--- Incidentes de las propiedades del anfitrión ---");
        List<Incidente> incidentes = anfitrion.revisarIncidentesDeSusPropiedades();

        if (incidentes.isEmpty()) {
            ConsoleUI.mostrarMensaje("No existen incidentes asociados a tus propiedades.");
            return;
        }

        mostrarListaIncidentes(incidentes);

        Incidente incidenteSeleccionado = seleccionarIncidentePorId(incidentes);
        if (incidenteSeleccionado == null) {
            return;
        }

        String opcion = ConsoleUI.leerTexto("¿Lo resuelve usted? (s/n): ");
        boolean resuelto = opcion.equalsIgnoreCase("s");
        manejadorIncidentes.manejar(incidenteSeleccionado, resuelto);
    }

    private void mostrarListaIncidentes(List<Incidente> incidentes) {
        for (Incidente incidente : incidentes) {
            ConsoleUI.mostrarMensaje("- " + incidente.getIdIncidente() + " | " + incidente.getDescripcion() + " | Estado: " + incidente.getEstado());
        }
    }

    private Incidente seleccionarIncidentePorId(List<Incidente> incidentes) {
        String idIncidente = ConsoleUI.leerTexto("Ingrese el ID del incidente a revisar: ");
        Incidente incidenteSeleccionado = incidentes.stream()
                .filter(i -> i.getIdIncidente().equalsIgnoreCase(idIncidente))
                .findFirst()
                .orElse(null);

        if (incidenteSeleccionado == null) {
            ConsoleUI.mostrarMensaje("No existe ese incidente en tus propiedades.");
        }
        return incidenteSeleccionado;
    }

    private void mostrarInformacionPropiedad() {
        Propiedad propiedadSeleccionada = seleccionarPropiedadDelAnfitrion();
        if (propiedadSeleccionada == null) {
            return;
        }

        propiedad = propiedadSeleccionada;
        ConsoleUI.mostrarMensaje("\n--- Información de la propiedad ---");
        ConsoleUI.mostrarMensaje("Nombre: " + propiedad.getNombre());
        ConsoleUI.mostrarMensaje("Dirección: " + propiedad.getDireccion());
        ConsoleUI.mostrarMensaje("Reglas: " + propiedad.getReglas());
        ConsoleUI.mostrarMensaje("Check-in: " + propiedad.getCheckIn());
        ConsoleUI.mostrarMensaje("Check-out: " + propiedad.getCheckOut());
        ConsoleUI.mostrarMensaje("Servicios: " + propiedad.getServicios());
        ConsoleUI.mostrarMensaje("Unidades: " + propiedad.getChildren().size());
    }

    private void registrarNuevaPropiedad() {
        ConsoleUI.mostrarMensaje("\n--- Registrar nueva propiedad con unidades ---");

        String[] datos = leerDatosPropiedad();
        List<CompPropiedad> unidades = leerUnidades();

        Propiedad propiedadNueva = anfitrion.registrarNuevaPropiedadConUnidades(
                datos[0], datos[1], datos[2], unidades, List.of(datos[3].split("\\s*,\\s*")));
        if (propiedadNueva == null) {
            ConsoleUI.mostrarMensaje("No se registró la propiedad porque no tiene unidades válidas.");
            return;
        }

        configurarPoliticaYHorarios(propiedadNueva, datos[4], datos[5]);
    }

    private String[] leerDatosPropiedad() {
        String nombre = ConsoleUI.leerTexto("Nombre de la propiedad: ");
        String direccion = ConsoleUI.leerTexto("Dirección: ");
        String reglas = ConsoleUI.leerTexto("Reglas: ");
        String checkIn = ConsoleUI.leerTexto("Hora de check-in (HH:mm, ej: 15:00): ");
        String checkOut = ConsoleUI.leerTexto("Hora de check-out (HH:mm, ej: 12:00): ");
        String serviciosStr = ConsoleUI.leerTexto("Servicios (separados por coma): ");
        return new String[] { nombre, direccion, reglas, serviciosStr, checkIn, checkOut };
    }

    private List<CompPropiedad> leerUnidades() {
        List<CompPropiedad> unidades = new ArrayList<>();

        ConsoleUI.mostrarMensaje("\nDebe registrar al menos una unidad para crear la propiedad.");
        agregarUnidadDesdeEntrada(unidades);

        String seguir = ConsoleUI.leerTexto("¿Desea añadir otra unidad? (s/n): ");
        while (seguir.equalsIgnoreCase("s")) {
            agregarUnidadDesdeEntrada(unidades);
            seguir = ConsoleUI.leerTexto("¿Desea añadir otra unidad? (s/n): ");
        }
        return unidades;
    }

    private void configurarPoliticaYHorarios(Propiedad propiedadNueva, String checkIn, String checkOut) {
        String nombrePolitica = ConsoleUI.leerTexto("Nombre de la política de cancelación (ej. Flexible, Moderada, Estricta): ");
        int diasAntelacion = ConsoleUI.leerInt("Días de anticipación mínimos para aplicar la política: ");
        double penalizacion = ConsoleUI.leerDouble("Factor de penalización (ej. 0.2 para 20% de penalización): ");

        PoliticaCancelacion politica = new PoliticaCancelacion(nombrePolitica, diasAntelacion, penalizacion);
        propiedadNueva.setPoliticaCancelacion(politica);

        anfitrion.actualizarHorarioDePropiedad(propiedadNueva,
                checkIn.isBlank() ? "15:00" : checkIn,
                checkOut.isBlank() ? "12:00" : checkOut);

        ConsoleUI.mostrarMensaje("Propiedad registrada correctamente con " + propiedadNueva.getChildren().size() + " unidad(es).");
        ConsoleUI.mostrarMensaje("Check-in: " + propiedadNueva.getCheckIn());
        ConsoleUI.mostrarMensaje("Check-out: " + propiedadNueva.getCheckOut());
    }

    private Propiedad seleccionarPropiedadDelAnfitrion() {
        List<Propiedad> propiedadesDelAnfitrion = anfitrion.getPropiedades();
        if (propiedadesDelAnfitrion == null || propiedadesDelAnfitrion.isEmpty()) {
            ConsoleUI.mostrarMensaje("Todavía no tienes propiedades registradas.");
            return null;
        }

        mostrarListaPropiedades(propiedadesDelAnfitrion);

        String entrada = ConsoleUI.leerTexto("Ingrese el nombre o número de la propiedad a gestionar: ");
        return parsearSeleccionPropiedad(propiedadesDelAnfitrion, entrada);
    }

    private void mostrarListaPropiedades(List<Propiedad> propiedadesDelAnfitrion) {
        ConsoleUI.mostrarMensaje("\nTus propiedades registradas:");
        for (int i = 0; i < propiedadesDelAnfitrion.size(); i++) {
            Propiedad p = propiedadesDelAnfitrion.get(i);
            ConsoleUI.mostrarMensaje((i + 1) + ". " + p.getNombre() + " | " + p.getDireccion());
        }
    }

    private Propiedad parsearSeleccionPropiedad(List<Propiedad> propiedadesDelAnfitrion, String entrada) {
        try {
            int indice = Integer.parseInt(entrada.trim());
            if (indice >= 1 && indice <= propiedadesDelAnfitrion.size()) {
                return propiedadesDelAnfitrion.get(indice - 1);
            }
        } catch (NumberFormatException ignored) {
        }

        Propiedad propiedadEncontrada = anfitrion.buscarPropiedadPorNombre(entrada.trim());
        if (propiedadEncontrada != null) {
            return propiedadEncontrada;
        }

        ConsoleUI.mostrarMensaje("No se encontró esa propiedad entre las de tu registro.");
        return null;
    }

    private Unidad seleccionarUnidadDePropiedad(Propiedad propiedadSeleccionada) {
        if (propiedadSeleccionada == null || propiedadSeleccionada.getChildren() == null || propiedadSeleccionada.getChildren().isEmpty()) {
            return null;
        }

        mostrarListaUnidades(propiedadSeleccionada);

        String idUnidad = ConsoleUI.leerTexto("Ingrese el ID de la unidad a gestionar: ");
        Unidad unidadEncontrada = propiedadSeleccionada.obtenerUnidadPorId(idUnidad);
        if (unidadEncontrada == null) {
            ConsoleUI.mostrarMensaje("No se encontró esa unidad en la propiedad seleccionada.");
            return null;
        }

        return unidadEncontrada;
    }

    private void mostrarListaUnidades(Propiedad propiedadSeleccionada) {
        ConsoleUI.mostrarMensaje("\nUnidades disponibles en la propiedad " + propiedadSeleccionada.getNombre() + ":");
        int contador = 1;
        for (CompPropiedad hijo : propiedadSeleccionada.getChildren()) {
            if (hijo instanceof Unidad unidad) {
                ConsoleUI.mostrarMensaje(contador + ". " + unidad.getIdUnidad() + " | " + unidad.getTipo() + " | " + unidad.getPrecio() + "/noche");
                contador++;
            }
        }
    }

    private void agregarUnidadDesdeEntrada(List<CompPropiedad> unidades) {
        String idUnidad = ConsoleUI.leerTexto("ID de la unidad: ");
        String tipo = ConsoleUI.leerTexto("Tipo de unidad (Casa / Departamento Completo / Habitación Privada): ");
        double precio = ConsoleUI.leerDouble("Precio base por noche: ");

        unidades.add(crearFactoryUnidad(tipo).crearUnidad(idUnidad, precio));
    }

    private UnidadFactory crearFactoryUnidad(String tipo) {
        String tipoNormalizado = tipo == null ? "" : tipo.trim().toLowerCase();

        return switch (tipoNormalizado) {
            case "departamento", "departamento completo" -> new com.example.FactoryMethod.DepartamentoFactory();
            case "habitacion privada", "habitacion", "habitación privada" -> new com.example.FactoryMethod.HabitacionPrivadaFactory();
            default -> {
                if (!tipoNormalizado.isBlank()) {
                    ConsoleUI.mostrarMensaje("Tipo no reconocido. Se usará Casa por defecto.");
                }
                yield new com.example.FactoryMethod.CasaFactory();
            }
        };
    }
}
