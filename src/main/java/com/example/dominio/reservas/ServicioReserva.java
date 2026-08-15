package com.example.dominio.reservas;

import java.util.Date;

import com.example.Composite.CompPropiedad;
import com.example.Composite.Unidad;
import com.example.Decorator.TarifaExtra;
import com.example.Decorator.TarifaSeguridad;
import com.example.Singleton.BaseDatosSingleton;
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

    public ServicioReserva(PasarelaPago pasarela,
                           SistemaNotificacion notificacion) {

        this.pasarela = pasarela;
        this.notificacion = notificacion;
    }

    public Reserva reservar(Date inicio,
                            Date fin,
                            Huesped huesped,
                            Unidad unidad,
                            ParametrosReserva parametros) {

        double totalCalculado = calcularTotalConExtras(unidad, parametros.aplicarTarifaExtra(), parametros.aplicarDepositoSeguridad());
        System.out.println("Iniciando proceso de reserva...");
        boolean creada = intentarReserva(inicio, fin, huesped, unidad, totalCalculado, parametros.metodoPago());
        if (!creada) {
            return null;
        }
        return obtenerReservaActiva(huesped, unidad, inicio, fin, totalCalculado);
    }

    public boolean intentarReserva(Date inicio,
                                   Date fin,
                                   Huesped huesped,
                                   Unidad unidad,
                                   double totalCalculado,
                                   MetodoPago metodoPago) {
        if (unidad == null || !unidadReservable(unidad, inicio, fin)) {
            System.out.println("La unidad no está disponible para esas fechas.");
            return false;
        }

        if (existeConflictoReserva(unidad, inicio, fin)) {
            System.out.println("Ya existe una reserva para esas fechas.");
            return false;
        }

        if (existeConflictoReservaHuesped(huesped, inicio, fin)) {
            System.out.println("El huésped ya tiene una reserva activa para esas fechas.");
            return false;
        }

        Reserva reserva = new Reserva(inicio, fin, huesped, unidad);
        reserva.setTotal(totalCalculado);
        reserva.confirmar();

        if (unidad != null && unidad.getPropiedad() != null) {
            reserva.setPoliticaCancelacion(unidad.getPropiedad().getPoliticaCancelacion());
        }

        unidad.ocupar();

        Pago pago = new Pago(reserva.getTotal(), metodoPago);
        boolean pagoExitoso = procesarPago(pago);
        if (!pagoExitoso) {
            unidad.liberar();
            return false;
        }

        BaseDatosSingleton db = BaseDatosSingleton.getInstance();
        db.guardarReserva(reserva);
        if (huesped != null) {
            huesped.registrarReserva(reserva);
        }

        enviarConfirmacion(reserva, huesped);
        return true;
    }

    private Reserva obtenerReservaActiva(Huesped huesped, Unidad unidad, Date inicio, Date fin, double total) {
        if (huesped == null || unidad == null) {
            return null;
        }

        Reserva reserva = new Reserva(inicio, fin, huesped, unidad);
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

    private boolean existeConflictoReserva(Unidad unidad, Date inicio, Date fin) {
        BaseDatosSingleton db = BaseDatosSingleton.getInstance();
        for (Reserva reserva : db.getReservas()) {
            if (reserva.getUnidad() == unidad && seSolapan(inicio, fin, reserva.getFechaInicio(), reserva.getFechaFin())) {
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

    public boolean estaDisponible(Unidad unidad, Date inicio, Date fin) {
        if (unidad == null) {
            return false;
        }
        if (!unidadReservable(unidad, inicio, fin)) {
            System.out.println("La unidad " + unidad.getIdUnidad()
                    + " no se puede reservar para el rango solicitado.");
            return false;
        }
        if (existeConflictoReserva(unidad, inicio, fin)) {
            System.out.println("La unidad " + unidad.getIdUnidad()
                    + " ya está reservada para el rango de fechas solicitado.");
            return false;
        }
        return true;
    }

    private boolean unidadReservable(Unidad unidad, Date inicio, Date fin) {
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
            return !existeConflictoReserva(unidad, inicio, fin);
        }

        return false;
    }

    private boolean existeConflictoReservaHuesped(Huesped huesped, Date inicio, Date fin) {
        if (huesped == null || inicio == null || fin == null) {
            return false;
        }

        for (Reserva reserva : huesped.historialReservas()) {
            if (!esReservaActivaValida(reserva, huesped)) {
                continue;
            }
            if (seSolapan(inicio, fin, reserva.getFechaInicio(), reserva.getFechaFin())) {
                return true;
            }
        }

        BaseDatosSingleton db = BaseDatosSingleton.getInstance();
        for (Reserva reserva : db.getReservas()) {
            if (!esReservaActivaValida(reserva, huesped)) {
                continue;
            }
            if (seSolapan(inicio, fin, reserva.getFechaInicio(), reserva.getFechaFin())) {
                return true;
            }
        }

        return false;
    }

    private boolean esReservaActivaValida(Reserva reserva, Huesped huesped) {
        return reserva.getUnidad() != null
                && reserva.getHuesped() == huesped
                && (reserva.getEstado() == EstadoReserva.PENDIENTE || reserva.getEstado() == EstadoReserva.CONFIRMADA);
    }

    private boolean seSolapan(Date inicioA, Date finA, Date inicioB, Date finB) {
        return !(finA.before(inicioB) || inicioA.after(finB));
    }
}
