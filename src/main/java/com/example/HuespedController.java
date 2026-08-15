package com.example;

import com.example.Composite.Propiedad;
import com.example.Composite.Unidad;
import com.example.Singleton.BaseDatosSingleton;
import com.example.dominio.CriterioBusqueda;
import com.example.dominio.incidentes.Incidente;
import com.example.Strategy.pagos.*;
import com.example.dominio.resenas.ServicioResena;
import com.example.dominio.reservas.PoliticaCancelacion;
import com.example.dominio.reservas.Reserva;
import com.example.dominio.reservas.ServicioReserva;
import com.example.dominio.usuarios.Anfitrion;
import com.example.dominio.usuarios.Huesped;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class HuespedController {

    private static final SimpleDateFormat FORMATO_FECHA = new SimpleDateFormat("yyyy-MM-dd");

    private final Huesped huesped;
    private final Anfitrion anfitrion;
    private final ServicioReserva servicioReserva;
    private final ServicioResena servicioResena;
    private Propiedad propiedad;
    private Unidad unidad;
    private Reserva reservaActiva;

    public HuespedController(Huesped huesped, Anfitrion anfitrion, ServicioReserva servicioReserva, ServicioResena servicioResena) {
        this.huesped = huesped;
        this.anfitrion = anfitrion;
        this.servicioReserva = servicioReserva;
        this.servicioResena = servicioResena;
    }

    public void ejecutar() {
        boolean volver = false;
        while (!volver) {
            ConsoleUI.mostrarMensaje("\n=== MENÚ HUÉSPED ===");
            ConsoleUI.mostrarMensaje("1. Buscar propiedades por criterios (ubicación, precio, tipo, servicios)");
            ConsoleUI.mostrarMensaje("2. Ver todas las propiedades");
            ConsoleUI.mostrarMensaje("3. Realizar reserva");
            ConsoleUI.mostrarMensaje("4. Ver / Cancelar reserva activa");
            ConsoleUI.mostrarMensaje("5. Reportar incidente");
            ConsoleUI.mostrarMensaje("6. Crear reseña");
            ConsoleUI.mostrarMensaje("7. Volver al menú de perfiles");
            String opcion = ConsoleUI.leerTexto("Seleccione una opción: ");

            switch (opcion.toLowerCase()) {
                case "1", "buscar" -> buscarPropiedadesPorCriterios();
                case "2", "todas", "ver" -> mostrarCatalogoCompleto();
                case "3", "reservar", "reserva" -> realizarReserva();
                case "4", "cancelar", "ver reserva" -> verOCancelarReserva();
                case "5", "incidente" -> gestionarIncidente();
                case "6", "reseña", "resena" -> crearResena();
                case "7", "volver", "v" -> volver = true;
                default -> ConsoleUI.mostrarMensaje("\nOpción no válida.");
            }
        }
    }

    private void buscarPropiedadesPorCriterios() {
        ConsoleUI.mostrarMensaje("\n--- Búsqueda de Propiedades por Criterios ---");
        String ciudad = ConsoleUI.leerTexto("Ubicación (Ciudad): ");
        double min = ConsoleUI.leerDouble("Precio mínimo por noche: ");
        double max = ConsoleUI.leerDouble("Precio máximo por noche: ");
        String tipo = ConsoleUI.leerTexto("Tipo de alojamiento (ej. Casa, Apartamento): ");
        String serviciosStr = ConsoleUI.leerTexto("Servicios requeridos (separados por coma, ej: WiFi, Estacionamiento): ");

        List<String> servicios = serviciosStr.isEmpty() ? List.of() : Arrays.asList(serviciosStr.split("\\s*,\\s*"));
        CriterioBusqueda criterio = new CriterioBusqueda(ciudad, min, max, tipo, servicios);

        List<Propiedad> resultados = huesped.buscarPropiedad(criterio);
        desplegarPropiedadesYUnidades(resultados);
    }

    private void mostrarCatalogoCompleto() {
        ConsoleUI.mostrarMensaje("\n--- Catálogo General de Propiedades ---");
        List<Propiedad> todos = BaseDatosSingleton.getInstance().getCatalogo();
        desplegarPropiedadesYUnidades(todos);
    }

    private void realizarReserva() {
        ConsoleUI.mostrarMensaje("\n--- Realizar Reserva de Unidad ---");
        List<Propiedad> catalogo = BaseDatosSingleton.getInstance().getCatalogo();

        if (catalogo.isEmpty()) {
            ConsoleUI.mostrarMensaje("No hay propiedades disponibles en el catálogo.");
            return;
        }

        desplegarPropiedadesYUnidades(catalogo);

        ConsoleUI.mostrarMensaje("\n--- Selección ---");
        String nombrePropiedad = ConsoleUI.leerTexto("Nombre de la propiedad a reservar: ");
        String idUnidad = ConsoleUI.leerTexto("ID de la unidad a reservar: ");

        Propiedad propiedadEncontrada = BaseDatosSingleton.getInstance().buscarPropiedadPorNombre(nombrePropiedad);
        Unidad unidadSeleccionada = propiedadEncontrada != null
                ? propiedadEncontrada.obtenerUnidadPorId(idUnidad)
                : null;

        if (propiedadEncontrada == null || unidadSeleccionada == null) {
            ConsoleUI.mostrarMensaje("Error: Propiedad o unidad no encontrada.");
            return;
        }

        unidad = unidadSeleccionada;
        propiedad = propiedadEncontrada;

        Date[] rangoFechas = leerRangoFechas("Fecha de inicio (YYYY-MM-DD): ", "Fecha de fin (YYYY-MM-DD): ");
        if (rangoFechas == null) {
            return;
        }
        Date inicio = rangoFechas[0];
        Date fin = rangoFechas[1];

        if (!servicioReserva.estaDisponible(unidad, inicio, fin)) {
            manejarUnidadNoDisponible(inicio, fin);
            return;
        }

        boolean[] extras = leerOpcionesExtras();
        boolean aplicarTarifaExtra = extras[0];
        boolean aplicarDepositoSeguridad = extras[1];

        double costoTotalServicio = servicioReserva.calcularTotalConExtras(unidad, aplicarTarifaExtra, aplicarDepositoSeguridad);
        mostrarDesglosePago(unidad, aplicarTarifaExtra, aplicarDepositoSeguridad, costoTotalServicio);

        MetodoPago metodoPago = seleccionarMetodoPago();
        if (metodoPago == null) {
            ConsoleUI.mostrarMensaje("No se pudo crear el método de pago. Se cancela la reserva.");
            return;
        }

        confirmarReserva(inicio, fin, aplicarTarifaExtra, aplicarDepositoSeguridad, metodoPago);
    }

    private void manejarUnidadNoDisponible(Date inicio, Date fin) {
        ConsoleUI.mostrarMensaje("\n[FLUJO ALTERNATIVO]: La unidad " + unidad.getIdUnidad()
                + " NO está disponible para las fechas seleccionadas.");
        ConsoleUI.mostrarMensaje("1. Seleccionar un rango de fechas diferente para esta unidad");
        ConsoleUI.mostrarMensaje("2. Cancelar el proceso de reserva y volver al menú principal");
        String eleccion = ConsoleUI.leerTexto("Elija una opción (1/2): ");
        if ("2".equals(eleccion)) {
            ConsoleUI.mostrarMensaje("Proceso de reserva cancelado por el usuario.");
            return;
        }
        realizarReserva();
    }

    private boolean[] leerOpcionesExtras() {
        ConsoleUI.mostrarMensaje("\n--- Opciones Adicionales de Reserva ---");
        boolean aplicarTarifaExtra = ConsoleUI.leerTexto("¿Desea aplicar Tarifa Adicional de Servicio (+$15.00)? (s/n): ").equalsIgnoreCase("s");
        boolean aplicarDepositoSeguridad = ConsoleUI.leerTexto("¿Desea incluir Depósito de Seguridad Reembolsable (+$25.00)? (s/n): ").equalsIgnoreCase("s");
        return new boolean[] { aplicarTarifaExtra, aplicarDepositoSeguridad };
    }

    private void mostrarDesglosePago(Unidad unidad, boolean tarifaExtra, boolean deposito, double total) {
        ConsoleUI.mostrarMensaje("\n--- Desglose de Pago ---");
        ConsoleUI.mostrarMensaje("Precio base por noche: $" + unidad.getPrecio());
        ConsoleUI.mostrarMensaje("Tarifa adicional de servicio: $" + (tarifaExtra ? "15.00" : "0.00"));
        ConsoleUI.mostrarMensaje("Depósito de seguridad: $" + (deposito ? "25.00 (Reembolsable)" : "0.00"));
        ConsoleUI.mostrarMensaje("TOTAL ESTIMADO: $" + total);
    }

    private void confirmarReserva(Date inicio, Date fin, boolean tarifaExtra, boolean deposito, MetodoPago metodoPago) {
        ConsoleUI.mostrarMensaje("\n--- Procesamiento de Pago ---");
        reservaActiva = servicioReserva.reservar(inicio, fin, huesped, unidad,
                new com.example.dominio.reservas.ParametrosReserva(tarifaExtra, deposito, metodoPago));

        if (reservaActiva != null) {
            ConsoleUI.mostrarMensaje("\n¡Pago procesado con éxito y Reserva Confirmada!");
            ConsoleUI.mostrarMensaje("Huésped: " + huesped.getNombre());
            ConsoleUI.mostrarMensaje("Propiedad: " + propiedad.getNombre());
            ConsoleUI.mostrarMensaje("Unidad: " + unidad.getIdUnidad() + " (" + unidad.getTipo() + ")");
            ConsoleUI.mostrarMensaje("Monto total calculado por el sistema: $" + reservaActiva.getTotal());
        } else {
            ConsoleUI.mostrarMensaje("Error inesperado en el sistema al registrar la reserva.");
        }
    }

    private void verOCancelarReserva() {
        ConsoleUI.mostrarMensaje("\n--- Ver / Cancelar Reserva ---");
        Reserva reservaSeleccionada = seleccionarReservaDelHuesped();
        if (reservaSeleccionada == null) {
            return;
        }

        mostrarDetallesReserva(reservaSeleccionada);

        PoliticaCancelacion politica = reservaSeleccionada.getPoliticaCancelacion();

        if (politica == null) {
            ConsoleUI.mostrarMensaje("La propiedad no tiene una política de cancelación definida por el anfitrión.");
            ConsoleUI.mostrarMensaje("No se puede procesar la cancelación en este momento.");
            return;
        }

        mostrarPoliticaCancelacion(politica, reservaSeleccionada.getTotal());

        String aceptar = ConsoleUI.leerTexto("¿Acepta la política de cancelación y desea continuar con la cancelación? (s/n): ");
        if (!aceptar.equalsIgnoreCase("s")) {
            ConsoleUI.mostrarMensaje("Cancelación no procesada.");
            return;
        }

        reservaSeleccionada.setPoliticaCancelacion(politica);
        double reembolso = reservaSeleccionada.cancelar();

        ConsoleUI.mostrarMensaje("Reserva cancelada con éxito. Reembolso total asignado: $" + reembolso);
    }

    private void mostrarDetallesReserva(Reserva reserva) {
        ConsoleUI.mostrarMensaje("Reserva a nombre de: " + reserva.getHuesped().getNombre());
        ConsoleUI.mostrarMensaje("Unidad: " + reserva.getUnidad().getIdUnidad());
        ConsoleUI.mostrarMensaje("Monto pagado: $" + reserva.getTotal());
        ConsoleUI.mostrarMensaje("Estado: " + reserva.getEstado());
    }

    private void mostrarPoliticaCancelacion(PoliticaCancelacion politica, double totalReserva) {
        ConsoleUI.mostrarMensaje("\n--- Política de Cancelación de la Propiedad ---");
        ConsoleUI.mostrarMensaje("Nombre: " + politica.getNombre());
        ConsoleUI.mostrarMensaje("Días de anticipación requeridos: " + politica.getDiasAntelacion());
        ConsoleUI.mostrarMensaje("Penalización: " + (politica.getPorcentajePenalizacion() * 100) + "%");
        ConsoleUI.mostrarMensaje("Reembolso estimado: $" + (totalReserva * (1 - politica.getPorcentajePenalizacion())));
    }

    private void gestionarIncidente() {
        ConsoleUI.mostrarMensaje("\n--- Gestión de Incidentes ---");
        Reserva reservaSeleccionada = seleccionarReservaDelHuesped();
        if (reservaSeleccionada == null) {
            return;
        }

        String descripcion = ConsoleUI.leerTexto("Descripción del problema: ");
        Incidente incidente = huesped.reportarIncidente(descripcion, reservaSeleccionada);
        BaseDatosSingleton.getInstance().guardarIncidente(incidente);

        ConsoleUI.mostrarMensaje("ID del incidente generado: " + incidente.getIdIncidente());
        ConsoleUI.mostrarMensaje("\nIncidente registrado. El anfitrión lo revisará.");
    }

    private void crearResena() {
        ConsoleUI.mostrarMensaje("\n--- Crear Reseña ---");
        Reserva reservaSeleccionada = seleccionarReservaDelHuesped();
        if (reservaSeleccionada == null) {
            return;
        }

        String comentario = ConsoleUI.leerTexto("Comentario: ");
        int calificacion = ConsoleUI.leerInt("Calificación (1-5): ");

        servicioResena.crearResena(huesped, comentario, calificacion, reservaSeleccionada);
        ConsoleUI.mostrarMensaje("Reseña creada con éxito. Total de reseñas registradas: " + servicioResena.getResenas().size());
    }

    private MetodoPago seleccionarMetodoPago() {
        ConsoleUI.mostrarMensaje("Seleccione el método de pago:");
        ConsoleUI.mostrarMensaje("1. Tarjeta");
        ConsoleUI.mostrarMensaje("2. PayPal");
        String opcion = ConsoleUI.leerTexto("Opción: ");

        return switch (opcion.toLowerCase()) {
            case "1", "tarjeta", "t" -> procesarPagoTarjeta();
            case "2", "paypal", "pay", "p" -> procesarPagoPayPal();
            default -> {
                ConsoleUI.mostrarMensaje("Opción de pago no válida.");
                yield null;
            }
        };
    }

    private MetodoPago procesarPagoTarjeta() {
        String numeroTarjeta = ConsoleUI.leerTexto("Ingrese el número de tarjeta: ");
        if (numeroTarjeta.equalsIgnoreCase("9999") || numeroTarjeta.equalsIgnoreCase("error") || numeroTarjeta.isBlank()) {
            ConsoleUI.mostrarMensaje("\n[FLUJO ALTERNATIVO - PAGO FALLIDO]: Se ha rechazado la transacción con la tarjeta/cuenta provista.");
            ConsoleUI.mostrarMensaje("Motivo: Fondos insuficientes o datos de pago no válidos.");
            ConsoleUI.mostrarMensaje("Cancelando el proceso de reserva... La unidad " + unidad.getIdUnidad() + " sigue estando DISPONIBLE.");
            return null;
        }
        return new TarjetaMetodoPago(numeroTarjeta);
    }

    private MetodoPago procesarPagoPayPal() {
        String correo = ConsoleUI.leerTexto("Ingrese el correo de PayPal: ");
        if (correo.equalsIgnoreCase("error") || correo.isBlank()) {
            ConsoleUI.mostrarMensaje("\n[FLUJO ALTERNATIVO - PAGO FALLIDO]: Se ha rechazado la transacción con PayPal.");
            ConsoleUI.mostrarMensaje("Motivo: Correo de PayPal no válido.");
            ConsoleUI.mostrarMensaje("Cancelando el proceso de reserva... La unidad " + unidad.getIdUnidad() + " sigue estando DISPONIBLE.");
            return null;
        }
        return new PayPalMetodoPago(correo);
    }

    private Reserva seleccionarReservaDelHuesped() {
        List<Reserva> reservas = huesped.historialReservas();

        if (reservas == null || reservas.isEmpty()) {
            ConsoleUI.mostrarMensaje("No tienes reservas registradas.");
            return null;
        }

        mostrarListaReservas(reservas);

        String entrada = ConsoleUI.leerTexto("Ingrese el número de la reserva: ");

        try {
            int indice = Integer.parseInt(entrada.trim());
            if (indice >= 1 && indice <= reservas.size()) {
                return reservas.get(indice - 1);
            }
        } catch (NumberFormatException ignored) {
        }

        ConsoleUI.mostrarMensaje("Número de reserva no válido.");
        return null;
    }

    private void mostrarListaReservas(List<Reserva> reservas) {
        ConsoleUI.mostrarMensaje("\nTus reservas:");
        for (int i = 0; i < reservas.size(); i++) {
            Reserva r = reservas.get(i);
            ConsoleUI.mostrarMensaje((i + 1) + ". Unidad: " + (r.getUnidad() != null ? r.getUnidad().getIdUnidad() : "N/A")
                    + " | Del " + FORMATO_FECHA.format(r.getFechaInicio())
                    + " al " + FORMATO_FECHA.format(r.getFechaFin())
                    + " | Estado: " + r.getEstado());
        }
    }

    private Date[] leerRangoFechas(String mensajeInicio, String mensajeFin) {
        while (true) {
            try {
                String fechaInicioStr = ConsoleUI.leerTexto("\n" + mensajeInicio);
                String fechaFinStr = ConsoleUI.leerTexto(mensajeFin);

                Date inicio = FORMATO_FECHA.parse(fechaInicioStr);
                Date fin = FORMATO_FECHA.parse(fechaFinStr);

                if (fin.before(inicio) || fin.equals(inicio)) {
                    ConsoleUI.mostrarMensaje("Error: La fecha de fin debe ser posterior a la fecha de inicio.");
                    continue;
                }

                return new Date[] { inicio, fin };
            } catch (Exception e) {
                ConsoleUI.mostrarMensaje("Error: Formato de fecha incorrecto (use YYYY-MM-DD). Intente nuevamente.");
            }
        }
    }

    private void desplegarPropiedadesYUnidades(List<Propiedad> listaPropiedades) {
        ConsoleUI.mostrarMensaje("\nPropiedades encontradas: " + listaPropiedades.size());
        if (listaPropiedades.isEmpty()) {
            ConsoleUI.mostrarMensaje("No se encontraron propiedades que coincidan con la búsqueda.");
            return;
        }

        for (Propiedad p : listaPropiedades) {
            ConsoleUI.mostrarMensaje("\n- Propiedad: " + p.getNombre() + " | Ubicación: " + p.getDireccion() + " | Servicios: " + p.getServicios());
            if (p.getChildren().isEmpty()) {
                ConsoleUI.mostrarMensaje("  (Sin unidades asociadas)");
                continue;
            }

            for (var child : p.getChildren()) {
                if (child instanceof Unidad u) {
                    ConsoleUI.mostrarMensaje("  * Unidad ID: " + u.getIdUnidad() + " | Tipo: " + u.getTipo() + " | Precio base: $" + u.getPrecio() + "/noche");
                }
            }
        }
    }
}
