package com.example.dominio.reservas;

import java.util.Date;

import com.example.Composite.CompPropiedad;
import com.example.Composite.Unidad;
import com.example.Decorator.TarifaExtra;
import com.example.Decorator.TarifaSeguridad;
import com.example.Singleton.Repositorio;
import com.example.Strategy.notificacion.SistemaNotificacion;
import com.example.Strategy.pagos.MetodoPago;
import com.example.Strategy.pagos.Pago;
import com.example.dominio.pagos.PasarelaPago;
import com.example.dominio.usuarios.Huesped;
import com.example.dominio.usuarios.Usuario;
import com.example.State.*;

public class ServicioReserva {

    private PasarelaPago pasarela;
    private SistemaNotificacion notificacion;
    private com.example.Singleton.Repositorio repositorio;

    public ServicioReserva(PasarelaPago pasarela,
                           SistemaNotificacion notificacion,
                           com.example.Singleton.Repositorio repositorio) {

        this.pasarela = pasarela;
        this.notificacion = notificacion;
        this.repositorio = repositorio;
    }

    public Reserva reservar(RangoFechas fechas,
                            Huesped huesped,
                            Unidad unidad,
                            ParametrosReserva parametros) {

        double totalCalculado = calcularTotal(unidad, parametros);
        System.out.println("Iniciando proceso de reserva...");
        boolean creada = intentarReserva(fechas, huesped, unidad, totalCalculado, parametros.metodoPago());
        if (!creada) {
            return null;
        }
        return crearReservaConfirmada(huesped, unidad, fechas, totalCalculado);
    }

    private double calcularTotal(Unidad unidad, ParametrosReserva parametros) {
        return calcularTotalConExtras(unidad, parametros.aplicarTarifaExtra(), parametros.aplicarDepositoSeguridad());
    }

    public boolean intentarReserva(RangoFechas fechas,
                                   Huesped huesped,
                                   Unidad unidad,
                                   double totalCalculado,
                                   MetodoPago metodoPago) {
        if (!validarDisponibilidad(fechas, huesped, unidad)) {
            return false;
        }

        Reserva reserva = crearReservaBase(fechas, huesped, unidad, totalCalculado);

        Pago pago = new Pago(reserva.getTotal(), metodoPago);
        boolean pagoExitoso = procesarPago(pago);
        if (!pagoExitoso) {
            unidad.liberar();
            return false;
        }

        persistirReserva(reserva, huesped);

        enviarConfirmacion(reserva, huesped);
        return true;
    }

    private boolean validarDisponibilidad(RangoFechas fechas, Huesped huesped, Unidad unidad) {
        if (unidad == null || !unidadReservable(unidad, fechas)) {
            System.out.println("La unidad no está disponible para esas fechas.");
            return false;
        }

        if (existeConflictoReserva(unidad, fechas)) {
            System.out.println("Ya existe una reserva para esas fechas.");
            return false;
        }

        if (existeConflictoReservaHuesped(huesped, fechas)) {
            System.out.println("El huésped ya tiene una reserva activa para esas fechas.");
            return false;
        }
        return true;
    }

    private Reserva crearReservaBase(RangoFechas fechas, Huesped huesped, Unidad unidad, double totalCalculado) {
        Reserva reserva = new Reserva(fechas, huesped, unidad);
        reserva.setTotal(totalCalculado);
        reserva.confirmar();

        if (unidad != null) {
            com.example.Composite.Propiedad propiedad = repositorio.buscarPropiedadPorUnidad(unidad);
            if (propiedad != null) {
                reserva.setPoliticaCancelacion(propiedad.getPoliticaCancelacion());
            }
        }

        unidad.ocupar();
        return reserva;
    }

    private void persistirReserva(Reserva reserva, Huesped huesped) {
        repositorio.guardarReserva(reserva);
        if (huesped != null) {
            huesped.registrarReserva(reserva);
        }
    }

    private Reserva crearReservaConfirmada(Huesped huesped, Unidad unidad, RangoFechas fechas, double total) {
        if (huesped == null || unidad == null) {
            return null;
        }

        Reserva reserva = new Reserva(fechas, huesped, unidad);
        reserva.setTotal(total);
        return reserva;
    }

    public double calcularTotalConExtras(Unidad unidad, boolean aplicarTarifaExtra, boolean aplicarDepositoSeguridad) {
        if (unidad == null) {
            return 0.0;
        }

        CompPropiedad costo = unidad;
        if (aplicarTarifaExtra) {
            costo = new TarifaExtra(costo);
        }
        if (aplicarDepositoSeguridad) {
            costo = new TarifaSeguridad(costo);
        }

        double total = costo.costo();
        System.out.println("Calculando total con extras: " + total);
        return total;
    }

    private boolean existeConflictoReserva(Unidad unidad, RangoFechas fechas) {
        for (Reserva reserva : repositorio.getReservas()) {
            if (reserva.getUnidad() == unidad && fechas.seSolapaCon(reserva.getRangoFechas())) {
                return true;
            }
        }
        return false;
    }

    public boolean procesarPago(Pago pago) {

        System.out.println("Procesando pago...");

        return pasarela.procesarTransaccion(pago);
    }

    public void enviarConfirmacion(Reserva reserva, Usuario usuario) {

        System.out.println("Enviando confirmación...");

        if (notificacion != null && usuario != null) {
            notificacion.enviarMensaje(usuario,
                    "Tu reserva fue confirmada");
        }
    }

    public boolean estaDisponible(Unidad unidad, RangoFechas fechas) {
        if (unidad == null) {
            return false;
        }
        if (!unidadReservable(unidad, fechas)) {
            System.out.println("La unidad " + unidad.getIdUnidad()
                    + " no se puede reservar para el rango solicitado.");
            return false;
        }
        if (existeConflictoReserva(unidad, fechas)) {
            System.out.println("La unidad " + unidad.getIdUnidad()
                    + " ya está reservada para el rango de fechas solicitado.");
            return false;
        }
        return true;
    }

    private boolean unidadReservable(Unidad unidad, RangoFechas fechas) {
        if (unidad == null) {
            return false;
        }

        EstadoUnidad estado = unidad.getEstado();

        if (estado instanceof Disponible) {
            return true;
        }

        if (estado instanceof Mantenimiento || estado instanceof FueraDeServicio) {
            return false;
        }

        if (estado instanceof Reservada || estado instanceof Ocupada) {
            return !existeConflictoReserva(unidad, fechas);
        }

        return false;
    }

    private boolean existeConflictoReservaHuesped(Huesped huesped, RangoFechas fechas) {
        if (huesped == null || fechas == null) {
            return false;
        }

        for (Reserva reserva : buscarReservasActivasDeHuesped(huesped)) {
            if (fechas.seSolapaCon(reserva.getRangoFechas())) {
                return true;
            }
        }

        return false;
    }

    private java.util.List<Reserva> buscarReservasActivasDeHuesped(Huesped huesped) {
        java.util.List<Reserva> activas = new java.util.ArrayList<>();
        if (huesped != null) {
            for (Reserva reserva : huesped.historialReservas()) {
                if (esReservaActivaValida(reserva, huesped)) {
                    activas.add(reserva);
                }
            }
            for (Reserva reserva : repositorio.getReservas()) {
                if (esReservaActivaValida(reserva, huesped) && !activas.contains(reserva)) {
                    activas.add(reserva);
                }
            }
        }
        return activas;
    }

    private boolean esReservaActivaValida(Reserva reserva, Huesped huesped) {
        return reserva.getUnidad() != null
                && reserva.getHuesped() == huesped
                && (reserva.getEstado() == EstadoReserva.PENDIENTE || reserva.getEstado() == EstadoReserva.CONFIRMADA);
    }
}
