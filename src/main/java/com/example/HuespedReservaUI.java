package com.example;

import com.example.Composite.Propiedad;
import com.example.Composite.Unidad;
import com.example.Singleton.Repositorio;
import com.example.Strategy.pagos.MetodoPago;
import com.example.Strategy.pagos.PayPalMetodoPago;
import com.example.Strategy.pagos.TarjetaMetodoPago;
import com.example.dominio.reservas.PoliticaCancelacion;
import com.example.dominio.reservas.RangoFechas;
import com.example.dominio.reservas.Reserva;
import com.example.dominio.reservas.ServicioReserva;
import com.example.dominio.usuarios.Huesped;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class HuespedReservaUI {

    private static final SimpleDateFormat FORMATO_FECHA = new SimpleDateFormat("yyyy-MM-dd");

    private final Huesped huesped;
    private final Repositorio repositorio;
    private final ServicioReserva servicioReserva;
    private final Scanner scanner;
    
    private Propiedad propiedadSeleccionada;
    private Unidad unidadSeleccionada;
    private Reserva reservaActiva;
    private final HuespedBusquedaUI busquedaUI;

    public HuespedReservaUI(Huesped huesped, Repositorio repositorio, ServicioReserva servicioReserva, Scanner scanner, HuespedBusquedaUI busquedaUI) {
        this.huesped = huesped;
        this.repositorio = repositorio;
        this.servicioReserva = servicioReserva;
        this.scanner = scanner;
        this.busquedaUI = busquedaUI;
    }

    public void realizarReserva() {
        mostrarMensaje("\n--- Realizar Reserva de Unidad ---");
        List<Propiedad> catalogo = repositorio.getCatalogo();

        if (catalogo.isEmpty()) {
            mostrarMensaje("No hay propiedades disponibles en el catálogo.");
            return;
        }

        busquedaUI.desplegarPropiedadesYUnidades(catalogo);

        mostrarMensaje("\n--- Selección ---");
        String nombrePropiedad = leerTexto("Nombre de la propiedad a reservar: ");
        String idUnidad = leerTexto("ID de la unidad a reservar: ");

        Propiedad propiedadEncontrada = repositorio.buscarPropiedadPorNombre(nombrePropiedad);
        Unidad unidad = propiedadEncontrada != null
                ? propiedadEncontrada.obtenerUnidadPorId(idUnidad)
                : null;

        if (propiedadEncontrada == null || unidad == null) {
            mostrarMensaje("Error: Propiedad o unidad no encontrada.");
            return;
        }

        this.unidadSeleccionada = unidad;
        this.propiedadSeleccionada = propiedadEncontrada;

        RangoFechas fechas = leerRangoFechas("Fecha de inicio (YYYY-MM-DD): ", "Fecha de fin (YYYY-MM-DD): ");
        if (fechas == null) {
            return;
        }

        if (!servicioReserva.estaDisponible(unidadSeleccionada, fechas)) {
            manejarUnidadNoDisponible(fechas);
            return;
        }

        boolean[] extras = leerOpcionesExtras();
        boolean aplicarTarifaExtra = extras[0];
        boolean aplicarDepositoSeguridad = extras[1];

        double costoTotalServicio = servicioReserva.calcularTotalConExtras(unidadSeleccionada, aplicarTarifaExtra, aplicarDepositoSeguridad);
        mostrarDesglosePago(unidadSeleccionada, aplicarTarifaExtra, aplicarDepositoSeguridad, costoTotalServicio);

        MetodoPago metodoPago = seleccionarMetodoPago();
        if (metodoPago == null) {
            mostrarMensaje("No se pudo crear el método de pago. Se cancela la reserva.");
            return;
        }

        confirmarReserva(fechas, aplicarTarifaExtra, aplicarDepositoSeguridad, metodoPago);
    }

    private void manejarUnidadNoDisponible(RangoFechas fechas) {
        mostrarMensaje("\n[FLUJO ALTERNATIVO]: La unidad " + unidadSeleccionada.getIdUnidad()
                + " NO está disponible para las fechas seleccionadas.");
        mostrarMensaje("1. Seleccionar un rango de fechas diferente para esta unidad");
        mostrarMensaje("2. Cancelar el proceso de reserva y volver al menú principal");
        String eleccion = leerTexto("Elija una opción (1/2): ");
        if ("2".equals(eleccion)) {
            mostrarMensaje("Proceso de reserva cancelado por el usuario.");
            return;
        }
        realizarReserva();
    }

    private boolean[] leerOpcionesExtras() {
        mostrarMensaje("\n--- Opciones Adicionales de Reserva ---");
        boolean aplicarTarifaExtra = leerTexto("¿Desea aplicar Tarifa Adicional de Servicio (+$15.00)? (s/n): ").equalsIgnoreCase("s");
        boolean aplicarDepositoSeguridad = leerTexto("¿Desea incluir Depósito de Seguridad Reembolsable (+$25.00)? (s/n): ").equalsIgnoreCase("s");
        return new boolean[] { aplicarTarifaExtra, aplicarDepositoSeguridad };
    }

    private void mostrarDesglosePago(Unidad unidad, boolean tarifaExtra, boolean deposito, double total) {
        mostrarMensaje("\n--- Desglose de Pago ---");
        mostrarMensaje("Precio base por noche: $" + unidad.getPrecio());
        mostrarMensaje("Tarifa adicional de servicio: $" + (tarifaExtra ? "15.00" : "0.00"));
        mostrarMensaje("Depósito de seguridad: $" + (deposito ? "25.00 (Reembolsable)" : "0.00"));
        mostrarMensaje("TOTAL ESTIMADO: $" + total);
    }

    private void confirmarReserva(RangoFechas fechas, boolean tarifaExtra, boolean deposito, MetodoPago metodoPago) {
        mostrarMensaje("\n--- Procesamiento de Pago ---");
        reservaActiva = servicioReserva.reservar(fechas, huesped, unidadSeleccionada,
                new com.example.dominio.reservas.ParametrosReserva(tarifaExtra, deposito, metodoPago));

        if (reservaActiva != null) {
            mostrarMensaje("\n¡Pago procesado con éxito y Reserva Confirmada!");
            mostrarMensaje("Huésped: " + huesped.getNombre());
            mostrarMensaje("Propiedad: " + propiedadSeleccionada.getNombre());
            mostrarMensaje("Unidad: " + unidadSeleccionada.getIdUnidad() + " (" + unidadSeleccionada.getTipo() + ")");
            mostrarMensaje("Monto total calculado por el sistema: $" + reservaActiva.getTotal());
        } else {
            mostrarMensaje("Error inesperado en el sistema al registrar la reserva.");
        }
    }

    public void verOCancelarReserva() {
        mostrarMensaje("\n--- Ver / Cancelar Reserva ---");
        Reserva reservaSeleccionada = seleccionarReservaDelHuesped();
        if (reservaSeleccionada == null) {
            return;
        }

        mostrarDetallesReserva(reservaSeleccionada);

        PoliticaCancelacion politica = reservaSeleccionada.getPoliticaCancelacion();

        if (politica == null) {
            mostrarMensaje("La propiedad no tiene una política de cancelación definida por el anfitrión.");
            mostrarMensaje("No se puede procesar la cancelación en este momento.");
            return;
        }

        mostrarPoliticaCancelacion(politica, reservaSeleccionada.getTotal());

        String aceptar = leerTexto("¿Acepta la política de cancelación y desea continuar con la cancelación? (s/n): ");
        if (!aceptar.equalsIgnoreCase("s")) {
            mostrarMensaje("Cancelación no procesada.");
            return;
        }

        reservaSeleccionada.setPoliticaCancelacion(politica);
        double reembolso = reservaSeleccionada.cancelar();

        mostrarMensaje("Reserva cancelada con éxito. Reembolso total asignado: $" + reembolso);
    }

    private void mostrarDetallesReserva(Reserva reserva) {
        mostrarMensaje("Reserva a nombre de: " + reserva.getHuesped().getNombre());
        mostrarMensaje("Unidad: " + reserva.getUnidad().getIdUnidad());
        mostrarMensaje("Monto pagado: $" + reserva.getTotal());
        mostrarMensaje("Estado: " + reserva.getEstado());
    }

    private void mostrarPoliticaCancelacion(PoliticaCancelacion politica, double totalReserva) {
        mostrarMensaje("\n--- Política de Cancelación de la Propiedad ---");
        mostrarMensaje("Nombre: " + politica.getNombre());
        mostrarMensaje("Días de anticipación requeridos: " + politica.getDiasAntelacion());
        mostrarMensaje("Penalización: " + (politica.getPorcentajePenalizacion() * 100) + "%");
        mostrarMensaje("Reembolso estimado: $" + (totalReserva * (1 - politica.getPorcentajePenalizacion())));
    }

    private MetodoPago seleccionarMetodoPago() {
        mostrarMensaje("Seleccione el método de pago:");
        mostrarMensaje("1. Tarjeta");
        mostrarMensaje("2. PayPal");
        String opcion = leerTexto("Opción: ");

        return switch (opcion.toLowerCase()) {
            case "1", "tarjeta", "t" -> procesarPagoTarjeta();
            case "2", "paypal", "pay", "p" -> procesarPagoPayPal();
            default -> {
                mostrarMensaje("Opción de pago no válida.");
                yield null;
            }
        };
    }

    private MetodoPago procesarPagoTarjeta() {
        String numeroTarjeta = leerTexto("Ingrese el número de tarjeta: ");
        if (numeroTarjeta.equalsIgnoreCase("9999") || numeroTarjeta.equalsIgnoreCase("error") || numeroTarjeta.isBlank()) {
            mostrarMensaje("\n[FLUJO ALTERNATIVO - PAGO FALLIDO]: Se ha rechazado la transacción con la tarjeta/cuenta provista.");
            mostrarMensaje("Motivo: Fondos insuficientes o datos de pago no válidos.");
            mostrarMensaje("Cancelando el proceso de reserva...");
            return null;
        }
        return new TarjetaMetodoPago(numeroTarjeta);
    }

    private MetodoPago procesarPagoPayPal() {
        String correo = leerTexto("Ingrese el correo de PayPal: ");
        if (correo.equalsIgnoreCase("error") || correo.isBlank()) {
            mostrarMensaje("\n[FLUJO ALTERNATIVO - PAGO FALLIDO]: Se ha rechazado la transacción con PayPal.");
            mostrarMensaje("Motivo: Correo de PayPal no válido.");
            mostrarMensaje("Cancelando el proceso de reserva...");
            return null;
        }
        return new PayPalMetodoPago(correo);
    }

    public Reserva seleccionarReservaDelHuesped() {
        List<Reserva> reservas = huesped.historialReservas();

        if (reservas == null || reservas.isEmpty()) {
            mostrarMensaje("No tienes reservas registradas.");
            return null;
        }

        mostrarListaReservas(reservas);

        String entrada = leerTexto("Ingrese el número de la reserva: ");

        try {
            int indice = Integer.parseInt(entrada.trim());
            if (indice >= 1 && indice <= reservas.size()) {
                return reservas.get(indice - 1);
            }
        } catch (NumberFormatException ignored) {
        }

        mostrarMensaje("Número de reserva no válido.");
        return null;
    }

    private void mostrarListaReservas(List<Reserva> reservas) {
        mostrarMensaje("\nTus reservas:");
        for (int i = 0; i < reservas.size(); i++) {
            Reserva r = reservas.get(i);
            mostrarMensaje((i + 1) + ". Unidad: " + (r.getUnidad() != null ? r.getUnidad().getIdUnidad() : "N/A")
                    + " | Del " + FORMATO_FECHA.format(r.getFechaInicio())
                    + " al " + FORMATO_FECHA.format(r.getFechaFin())
                    + " | Estado: " + r.getEstado());
        }
    }

    private RangoFechas leerRangoFechas(String mensajeInicio, String mensajeFin) {
        while (true) {
            try {
                String fechaInicioStr = leerTexto("\n" + mensajeInicio);
                String fechaFinStr = leerTexto(mensajeFin);

                Date inicio = FORMATO_FECHA.parse(fechaInicioStr);
                Date fin = FORMATO_FECHA.parse(fechaFinStr);

                return new RangoFechas(inicio, fin);
            } catch (IllegalArgumentException e) {
                mostrarMensaje("Error: La fecha de fin debe ser posterior a la fecha de inicio.");
            } catch (Exception e) {
                mostrarMensaje("Error: Formato de fecha incorrecto (use YYYY-MM-DD). Intente nuevamente.");
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
