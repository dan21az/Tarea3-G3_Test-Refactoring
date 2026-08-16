package com.example;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.Composite.Propiedad;
import com.example.Composite.Unidad;
import com.example.Singleton.Repositorio;
import com.example.Singleton.RepositorioMemoria;
import com.example.State.FueraDeServicio;
import com.example.State.Mantenimiento;
import com.example.State.Ocupada;
import com.example.State.Reservada;
import com.example.Strategy.notificacion.Canal;
import com.example.Strategy.notificacion.Email;
import com.example.Strategy.notificacion.SistemaNotificacion;
import com.example.Strategy.pagos.MetodoPago;
import com.example.Strategy.pagos.Pago;
import com.example.Strategy.pagos.PayPalMetodoPago;
import com.example.Strategy.pagos.TarjetaMetodoPago;
import com.example.dominio.pagos.PasarelaAdapter;
import com.example.dominio.pagos.PasarelaPago;
import com.example.dominio.pagos.ServicioPasarelaExterno;
import com.example.dominio.reservas.EstadoReserva;
import com.example.dominio.reservas.ParametrosReserva;
import com.example.dominio.reservas.PoliticaCancelacion;
import com.example.dominio.reservas.RangoFechas;
import com.example.dominio.reservas.Reserva;
import com.example.dominio.reservas.ServicioReserva;
import com.example.dominio.usuarios.Huesped;

class ServicioReservaTest {

    private Repositorio db;
    private PasarelaPago pasarela;
    private SistemaNotificacion notificacion;
    private ServicioReserva servicio;
    private Huesped huesped;
    private Date inicio;
    private Date fin;

    static class CanalRegistrador implements Canal {
        String ultimoMensaje;
        int vecesEnviado = 0;

        @Override
        public void enviar(String mensaje) {
            this.ultimoMensaje = mensaje;
            this.vecesEnviado++;
        }
    }

    @BeforeEach
    void setUp() {
        db = new RepositorioMemoria();
        db.limpiar();
        
        pasarela = new PasarelaAdapter(new ServicioPasarelaExterno());
        notificacion = new SistemaNotificacion(new Email());
        servicio = new ServicioReserva(pasarela, notificacion, db);
        huesped = new Huesped("H1", "Laura", "laura@mail.com", "123");
        inicio = new GregorianCalendar(2026, Calendar.SEPTEMBER, 1).getTime();
        fin = new GregorianCalendar(2026, Calendar.SEPTEMBER, 5).getTime();
    }

    @Test
    @DisplayName("R001 - Validar flujo completo de reserva exitosa con pago base")
    void r001_reservar_flujoExitoso() {
        Propiedad propiedad = new Propiedad("Casa Azul", "Bogotá", "No mascotas");
        Unidad unidad = new Unidad("U-100", "Casa", 120.0);
        propiedad.añadirUnidad(unidad);
        db.guardarPropiedad(propiedad);

        ParametrosReserva parametros = new ParametrosReserva(false, false,
                new TarjetaMetodoPago("4111111111111111"));

        Reserva reserva = servicio.reservar(new RangoFechas(inicio, fin), huesped, unidad, parametros);

        assertNotNull(reserva);
        assertEquals(120.0, reserva.getTotal(), 0.0);
        assertTrue(unidad.getEstado() instanceof Reservada
                || unidad.getEstado() instanceof Ocupada);
    }

    @Test
    @DisplayName("R002 - Verificar calculo de total con tarifa extra y deposito de seguridad")
    void r002_calcularTotalConExtras_aplicaExtraYDeposito() {
        Unidad unidad = new Unidad("U-1", "Casa", 100.0);

        assertEquals(140.0, servicio.calcularTotalConExtras(unidad, true, true), 0.0);
    }

    @Test
    @DisplayName("R003 - Verificar que sin extras el total es solo el precio base")
    void r003_calcularTotalConExtras_sinExtras() {
        Unidad unidad = new Unidad("U-1", "Casa", 100.0);

        assertEquals(100.0, servicio.calcularTotalConExtras(unidad, false, false), 0.0);
    }

    @Test
    @DisplayName("R004 - Validar manejo de unidad nula en el calculo de total")
    void r004_calcularTotalConExtras_unidadNula() {
        assertEquals(0.0, servicio.calcularTotalConExtras(null, true, true), 0.0);
    }

    @Test
    @DisplayName("R005 - Verificar aplicacion de solo la tarifa extra")
    void r005_calcularTotalConExtras_soloExtra() {
        Unidad unidad = new Unidad("U-1", "Casa", 50.0);

        assertEquals(65.0, servicio.calcularTotalConExtras(unidad, true, false), 0.0);
    }

    @Test
    @DisplayName("R006 - Verificar aplicacion de solo el deposito de seguridad")
    void r006_calcularTotalConExtras_soloDeposito() {
        Unidad unidad = new Unidad("U-1", "Casa", 50.0);

        assertEquals(75.0, servicio.calcularTotalConExtras(unidad, false, true), 0.0);
    }

    @Test
    @DisplayName("R007 - Validar disponibilidad para unidad libre")
    void r007_estaDisponible_unidadLibre() {
        Unidad unidad = new Unidad("U-1", "Casa", 100.0);

        assertTrue(servicio.estaDisponible(unidad, new RangoFechas(inicio, fin)));
    }

    @Test
    @DisplayName("R008 - Manejar unidad nula en disponibilidad")
    void r008_estaDisponible_unidadNula() {
        assertFalse(servicio.estaDisponible(null, new RangoFechas(inicio, fin)));
    }

    @Test
    @DisplayName("R009 - Unidad en mantenimiento no reservarable")
    void r009_estaDisponible_unidadEnMantenimiento() {
        Unidad unidad = new Unidad("U-1", "Casa", 100.0);
        unidad.cambiarEstado(new Mantenimiento());

        assertFalse(servicio.estaDisponible(unidad, new RangoFechas(inicio, fin)));
    }

    @Test
    @DisplayName("R010 - Unidad fuera de servicio no reservarable")
    void r010_estaDisponible_unidadFueraDeServicio() {
        Unidad unidad = new Unidad("U-1", "Casa", 100.0);
        unidad.cambiarEstado(new FueraDeServicio());

        assertFalse(servicio.estaDisponible(unidad, new RangoFechas(inicio, fin)));
    }

    @Test
    @DisplayName("R011 - Detectar conflicto de fechas para la misma unidad")
    void r011_intentarReserva_conflictoFechasUnidad() {
        Propiedad propiedad = new Propiedad("Casa Azul", "Bogotá", "r");
        Unidad unidad = new Unidad("U-100", "Casa", 100.0);
        propiedad.añadirUnidad(unidad);
        db.guardarPropiedad(propiedad);

        Huesped huespedA = new Huesped("H-A", "Laura", "laura@mail.com", "123");
        Reserva existente = new Reserva(new RangoFechas(inicio, fin), huespedA, unidad);
        existente.setTotal(100.0);
        existente.confirmar();
        db.guardarReserva(existente);

        Huesped huespedB = new Huesped("H-B", "Pedro", "pedro@mail.com", "123");
        MetodoPago card = new TarjetaMetodoPago("4111111111111111");
        boolean result = servicio.intentarReserva(new RangoFechas(inicio, fin), huespedB, unidad, 100.0, card);

        assertFalse(result);
    }

    @Test
    @DisplayName("R012 - Evitar doble reserva del mismo huesped")
    void r012_intentarReserva_conflictoHuesped() {
        Propiedad prop1 = new Propiedad("Casa Azul", "Bogotá", "r");
        Unidad unidad1 = new Unidad("U-100", "Casa", 100.0);
        prop1.añadirUnidad(unidad1);
        db.guardarPropiedad(prop1);

        Propiedad prop2 = new Propiedad("Casa Roja", "Medellin", "r");
        Unidad unidad2 = new Unidad("U-200", "Apartamento", 100.0);
        prop2.añadirUnidad(unidad2);
        db.guardarPropiedad(prop2);

        Reserva existente = new Reserva(new RangoFechas(inicio, fin), huesped, unidad1);
        existente.setTotal(100.0);
        existente.confirmar();
        db.guardarReserva(existente);
        huesped.registrarReserva(existente);

        MetodoPago card = new TarjetaMetodoPago("4111111111111111");
        boolean result = servicio.intentarReserva(new RangoFechas(inicio, fin), huesped, unidad2, 100.0, card);

        assertFalse(result);
    }

    @Test
    @DisplayName("R013 - Frontera: solapamiento exacto de fechas")
    void r013_intentarReserva_fronteraSolapamientoExacto() {
        Propiedad propiedad = new Propiedad("Casa Azul", "Bogota", "r");
        Unidad unidad = new Unidad("U-100", "Casa", 100.0);
        propiedad.añadirUnidad(unidad);
        db.guardarPropiedad(propiedad);

        Date inicioExistente = new GregorianCalendar(2026, Calendar.SEPTEMBER, 1).getTime();
        Date finExistente = new GregorianCalendar(2026, Calendar.SEPTEMBER, 3).getTime();

        Huesped huespedA = new Huesped("H-A", "Laura", "laura@mail.com", "123");
        Reserva existente = new Reserva(new RangoFechas(inicioExistente, finExistente), huespedA, unidad);
        existente.setTotal(100.0);
        existente.confirmar();
        db.guardarReserva(existente);

        Date nuevaInicio = new GregorianCalendar(2026, Calendar.SEPTEMBER, 3).getTime();
        Date nuevaFin = new GregorianCalendar(2026, Calendar.SEPTEMBER, 7).getTime();

        Huesped huespedB = new Huesped("H-B", "Pedro", "pedro@mail.com", "123");
        MetodoPago card = new TarjetaMetodoPago("4111111111111111");
        boolean result = servicio.intentarReserva(new RangoFechas(nuevaInicio, nuevaFin), huespedB, unidad, 100.0, card);

        assertFalse(result);
    }

    @Test
    @DisplayName("R014 - Unidad en mantenimiento no puede reservarse")
    void r014_intentarReserva_unidadEnMantenimiento() {
        Propiedad propiedad = new Propiedad("Casa Azul", "Bogota", "r");
        Unidad unidad = new Unidad("U-100", "Casa", 100.0);
        propiedad.añadirUnidad(unidad);
        db.guardarPropiedad(propiedad);
        unidad.cambiarEstado(new Mantenimiento());

        MetodoPago card = new TarjetaMetodoPago("4111111111111111");
        boolean result = servicio.intentarReserva(new RangoFechas(inicio, fin), huesped, unidad, 100.0, card);

        assertFalse(result);
    }

    @Test
    @DisplayName("R015 - Reserva exitosa devuelve true")
    void r015_intentarReserva_reservaExitosa() {
        Propiedad propiedad = new Propiedad("Casa Azul", "Bogota", "r");
        Unidad unidad = new Unidad("U-100", "Casa", 100.0);
        propiedad.añadirUnidad(unidad);
        db.guardarPropiedad(propiedad);

        MetodoPago card = new TarjetaMetodoPago("4111111111111111");
        boolean result = servicio.intentarReserva(new RangoFechas(inicio, fin), huesped, unidad, 100.0, card);

        assertTrue(result);
        assertTrue(unidad.getEstado() instanceof Reservada);
    }

    @Test
    @DisplayName("R016 - Procesar pago exitoso con tarjeta valida")
    void r016_procesarPago_tarjetaValida() {
        Pago pago = new Pago(120.0, new TarjetaMetodoPago("4111111111111111"));

        assertTrue(servicio.procesarPago(pago));
    }

    @Test
    @DisplayName("R017 - Procesar pago fallido cuando la pasarela rechaza")
    void r017_procesarPago_pagoFallido() {
        PasarelaPago pasarelaFallida = pago -> false;
        ServicioReserva servicioFallido = new ServicioReserva(pasarelaFallida, notificacion, db);
        Pago pago = new Pago(120.0, new TarjetaMetodoPago("4111111111111111"));

        assertFalse(servicioFallido.procesarPago(pago));
    }

    @Test
    @DisplayName("R018 - Procesar pago con objeto Pago nulo lanza excepcion")
    void r018_procesarPago_pagoNulo() {
        assertThrows(NullPointerException.class, () -> servicio.procesarPago(null));
    }

    @Test
    @DisplayName("R019 - Procesar pago verifica monto pasado a la pasarela")
    void r019_procesarPago_verificaMonto() {
        final double[] montoCapturado = {0.0};
        PasarelaPago capturadora = pago -> {
            montoCapturado[0] = pago.getMonto();
            return true;
        };
        ServicioReserva servicioCaptura = new ServicioReserva(capturadora, notificacion, db);
        Pago pago = new Pago(150.0, new TarjetaMetodoPago("4111111111111111"));

        assertTrue(servicioCaptura.procesarPago(pago));
        assertEquals(150.0, montoCapturado[0], 0.0);
    }

    @Test
    @DisplayName("R020 - Procesar pago exitoso con PayPal")
    void r020_procesarPago_paypal() {
        Pago pago = new Pago(200.0, new PayPalMetodoPago("usuario@correo.com"));

        assertTrue(servicio.procesarPago(pago));
    }

    @Test
    @DisplayName("R021 - Enviar confirmacion con reserva nula no lanza excepcion")
    void r021_enviarConfirmacion_reservaNula() {
        assertDoesNotThrow(() -> servicio.enviarConfirmacion(null, huesped));
    }

    @Test
    @DisplayName("R022 - Enviar confirmacion verifica contenido del mensaje")
    void r022_enviarConfirmacion_verificaContenidoMensaje() {
        CanalRegistrador canal = new CanalRegistrador();
        ServicioReserva servicioConCanal = new ServicioReserva(pasarela,
                new SistemaNotificacion(canal), db);
        Reserva reserva = new Reserva(new RangoFechas(inicio, fin), huesped, new Unidad("U-1", "Casa", 100.0));

        servicioConCanal.enviarConfirmacion(reserva, huesped);

        assertEquals(1, canal.vecesEnviado);
        assertEquals("Tu reserva fue confirmada", canal.ultimoMensaje);
    }

    @Test
    @DisplayName("R023 - SistemaNotificacion con canal nullo lanza excepcion al enviar")
    void r023_sistemaNotificacion_canalNulo() {
        SistemaNotificacion notif = new SistemaNotificacion(null);

        assertThrows(NullPointerException.class,
                () -> notif.enviarMensaje(huesped, "Tu reserva fue confirmada"));
    }

    @Test
    @DisplayName("R024 - setCanal con valor nullo no lanza excepcion")
    void r024_sistemaNotificacion_setCanalNulo() {
        SistemaNotificacion notif = new SistemaNotificacion(new Email());

        assertDoesNotThrow(() -> notif.setCanal(null));
    }

    @Test
    @DisplayName("R025 - SistemaNotificacion con usuario nullo lanza excepcion")
    void r025_sistemaNotificacion_usuarioNulo() {
        CanalRegistrador canal = new CanalRegistrador();
        SistemaNotificacion notif = new SistemaNotificacion(canal);

        assertThrows(NullPointerException.class, () -> notif.enviarMensaje(null, "msg"));
    }

    @Test
    @DisplayName("R026 - SistemaNotificacion con mensaje nullo lo recibe sin excepcion")
    void r026_sistemaNotificacion_mensajeNulo() {
        CanalRegistrador canal = new CanalRegistrador();
        SistemaNotificacion notif = new SistemaNotificacion(canal);

        notif.enviarMensaje(huesped, null);

        assertEquals(1, canal.vecesEnviado);
        assertNull(canal.ultimoMensaje);
    }

    @Test
    @DisplayName("R027 - Envio de confirmacion tras reserva exitosa (integracion)")
    void r027_enviarConfirmacion_integration() {
        CanalRegistrador canal = new CanalRegistrador();
        ServicioReserva servicioConCanal = new ServicioReserva(pasarela,
                new SistemaNotificacion(canal), db);

        Propiedad propiedad = new Propiedad("Casa Azul", "Bogota", "No mascotas");
        Unidad unidad = new Unidad("U-100", "Casa", 120.0);
        propiedad.añadirUnidad(unidad);
        db.guardarPropiedad(propiedad);

        ParametrosReserva parametros = new ParametrosReserva(false, false,
                new TarjetaMetodoPago("4111111111111111"));
        Reserva reserva = servicioConCanal.reservar(new RangoFechas(inicio, fin), huesped, unidad, parametros);

        assertNotNull(reserva);
        assertEquals(120.0, reserva.getTotal(), 0.0);
        assertEquals(1, canal.vecesEnviado);
        assertEquals("Tu reserva fue confirmada", canal.ultimoMensaje);
    }

    @Test
    @DisplayName("R028 - Enviar confirmacion con usuario nulo no envia")
    void r028_enviarConfirmacion_usuarioNulo() {
        CanalRegistrador canal = new CanalRegistrador();
        ServicioReserva servicioConCanal = new ServicioReserva(pasarela,
                new SistemaNotificacion(canal), db);
        Reserva reserva = new Reserva(new RangoFechas(inicio, fin), huesped, new Unidad("U-1", "Casa", 100.0));

        servicioConCanal.enviarConfirmacion(reserva, null);

        assertEquals(0, canal.vecesEnviado);
    }

    @Test
    @DisplayName("R029 - Enviar confirmacion con notificacion nula no lanza excepcion")
    void r029_enviarConfirmacion_notificacionNula() {
        ServicioReserva servicioSinNotif = new ServicioReserva(pasarela, null, db);
        Reserva reserva = new Reserva(new RangoFechas(inicio, fin), huesped, new Unidad("U-1", "Casa", 100.0));

        assertDoesNotThrow(() -> servicioSinNotif.enviarConfirmacion(reserva, huesped));
    }

    @Test
    @DisplayName("R030 - SistemaNotificacion envia mensaje por canal configurado")
    void r030_sistemaNotificacion_enviarMensaje() {
        CanalRegistrador canal = new CanalRegistrador();
        SistemaNotificacion notif = new SistemaNotificacion(canal);

        notif.enviarMensaje(huesped, "Tu reserva fue confirmada");

        assertEquals(1, canal.vecesEnviado);
        assertEquals("Tu reserva fue confirmada", canal.ultimoMensaje);
    }

    @Test
    @DisplayName("R031 - Strategy: cambio dinamico de canal de Email a SMS")
    void r031_sistemaNotificacion_setCanal() {
        CanalRegistrador canalEmail = new CanalRegistrador();
        SistemaNotificacion notif = new SistemaNotificacion(canalEmail);

        notif.enviarMensaje(huesped, "msg email");
        assertEquals(1, canalEmail.vecesEnviado);

        CanalRegistrador canalSms = new CanalRegistrador();
        notif.setCanal(canalSms);
        notif.enviarMensaje(huesped, "msg sms");

        assertEquals(1, canalSms.vecesEnviado);
        assertEquals("msg sms", canalSms.ultimoMensaje);
        assertEquals("msg email", canalEmail.ultimoMensaje);
    }

    @Test
    @DisplayName("R032 - Cancelar con politica aplicada devuelve reembolso del 80%")
    void r032_cancelar_politicaAplicada() {
        PoliticaCancelacion politica = new PoliticaCancelacion("Estricta", 7, 0.2);
        Reserva reserva = new Reserva(new RangoFechas(inicio, fin), huesped, new Unidad("U-1", "Casa", 120.0));
        reserva.setTotal(120.0);
        reserva.setPoliticaCancelacion(politica);

        double reembolso = reserva.cancelar();

        assertEquals(EstadoReserva.CANCELADA, reserva.getEstado());
        assertEquals(96.0, reembolso, 0.0);
    }

    @Test
    @DisplayName("R033 - Cancelar sin importe devuelve reembolso 0")
    void r033_cancelar_totalCero() {
        PoliticaCancelacion politica = new PoliticaCancelacion("Estricta", 7, 0.2);
        Reserva reserva = new Reserva(new RangoFechas(inicio, fin), huesped, new Unidad("U-1", "Casa", 0.0));
        reserva.setTotal(0.0);
        reserva.setPoliticaCancelacion(politica);

        double reembolso = reserva.cancelar();

        assertEquals(EstadoReserva.CANCELADA, reserva.getEstado());
        assertEquals(0.0, reembolso, 0.0);
    }

    @Test
    @DisplayName("R034 - Cancelar sin politica definida devuelve reembolso 0")
    void r034_cancelar_politicaNula() {
        Reserva reserva = new Reserva(new RangoFechas(inicio, fin), huesped, new Unidad("U-1", "Casa", 120.0));
        reserva.setTotal(120.0);

        double reembolso = reserva.cancelar();

        assertEquals(EstadoReserva.CANCELADA, reserva.getEstado());
        assertEquals(0.0, reembolso, 0.0);
    }

    @Test
    @DisplayName("R035 - Obtener politica de cancelacion desde la propiedad")
    void r035_getPoliticaCancelacion_desdePropiedad() {
        PoliticaCancelacion politica = new PoliticaCancelacion("Estricta", 7, 0.2);
        Unidad unidad = new Unidad("U-100", "Casa", 100.0);
        Propiedad propiedad = new Propiedad("Casa Azul", "Bogota", "r");
        propiedad.añadirUnidad(unidad);
        propiedad.setPoliticaCancelacion(politica);

        Reserva reserva = new Reserva(new RangoFechas(inicio, fin), huesped, unidad);

        assertEquals(politica, reserva.getPoliticaCancelacion());
    }

    @Test
    @DisplayName("R036 - Obtener politica de cancelacion con unidad nula devuelve null")
    void r036_getPoliticaCancelacion_unidadNula() {
        Reserva reserva = new Reserva(new RangoFechas(inicio, fin), huesped, null);

        assertNull(reserva.getPoliticaCancelacion());
    }

    @Test
    @DisplayName("R037 - Confirmar reserva pasa de PENDIENTE a CONFIRMADA")
    void r037_confirmar_transicionAEstadoConfirmada() {
        Reserva reserva = new Reserva(new RangoFechas(inicio, fin), huesped, new Unidad("U-1", "Casa", 100.0));

        assertEquals(EstadoReserva.PENDIENTE, reserva.getEstado());

        reserva.confirmar();

        assertEquals(EstadoReserva.CONFIRMADA, reserva.getEstado());
    }
}
