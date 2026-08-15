package com.example;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.ChainOfResponsibility.AnfitrionHandler;
import com.example.ChainOfResponsibility.ModeradorHandler;
import com.example.ChainOfResponsibility.SoporteLegalHandler;
import com.example.Composite.Propiedad;
import com.example.Composite.Unidad;
import com.example.Singleton.BaseDatosSingleton;
import com.example.State.Disponible;
import com.example.State.Reservada;
import com.example.Strategy.notificacion.Email;
import com.example.Strategy.notificacion.SistemaNotificacion;
import com.example.Strategy.pagos.MetodoPago;
import com.example.Strategy.pagos.TarjetaMetodoPago;
import com.example.dominio.CriterioBusqueda;
import com.example.dominio.incidentes.EstadoIncidente;
import com.example.dominio.incidentes.Incidente;
import com.example.dominio.pagos.PasarelaPago;
import com.example.dominio.resenas.Resena;
import com.example.dominio.resenas.ServicioResena;
import com.example.dominio.reservas.EstadoReserva;
import com.example.dominio.reservas.ParametrosReserva;
import com.example.dominio.reservas.PoliticaCancelacion;
import com.example.dominio.reservas.Reserva;
import com.example.dominio.reservas.ServicioReserva;
import com.example.dominio.usuarios.Anfitrion;
import com.example.dominio.usuarios.Huesped;
import com.example.dominio.usuarios.Moderador;
import com.example.dominio.usuarios.SoporteLegal;

class SistemaTest {

    private BaseDatosSingleton db;
    private ServicioReserva servicioReserva;
    private ServicioResena servicioResena;
    private Huesped huesped;
    private Anfitrion anfitrion;
    private Date inicio;
    private Date fin;

    @BeforeEach
    void setUp() {
        db = BaseDatosSingleton.getInstance();
        db.limpiar();

        huesped = new Huesped("H-001", "Laura", "laura@mail.com", "123");
        anfitrion = new Anfitrion("A-001", "Carlos", "carlos@mail.com", "123");

        servicioReserva = new ServicioReserva(
                new com.example.dominio.pagos.PasarelaAdapter(new com.example.dominio.pagos.ServicioPasarelaExterno()),
                new SistemaNotificacion(new Email()));
        servicioResena = new ServicioResena();

        inicio = new GregorianCalendar(2026, Calendar.OCTOBER, 1).getTime();
        fin = new GregorianCalendar(2026, Calendar.OCTOBER, 5).getTime();
    }

    /* ---------- S001-S002: Flujos integrados ---------- */

    @Test
    @DisplayName("S001 - Validar flujo completo de reserva y cancelación")
    void s001_buscarReservarPagarCancelar_flujoCompleto() {
        Propiedad propiedad = new Propiedad("Casa Azul", "Bogotá", "No mascotas");
        propiedad.setPoliticaCancelacion(new PoliticaCancelacion("Flexible", 2, 0.2));
        Unidad unidad = new Unidad("U-100", "Casa", 120.0);
        propiedad.añadirUnidad(unidad);
        db.guardarPropiedad(propiedad);

        CriterioBusqueda criterio = new CriterioBusqueda("Bogotá", 0, 0, "Casa", List.of());
        List<Propiedad> resultados = huesped.buscarPropiedad(criterio);

        assertEquals(1, resultados.size());
        assertEquals("Casa Azul", resultados.get(0).getNombre());

        MetodoPago card = new TarjetaMetodoPago("4111111111111111");
        ParametrosReserva parametros = new ParametrosReserva(false, false, card);
        Reserva reserva = servicioReserva.reservar(inicio, fin, huesped, unidad, parametros);

        assertNotNull(reserva);

        Reserva reservaConfirmada = db.getReservas().get(0);
        assertEquals(EstadoReserva.CONFIRMADA, reservaConfirmada.getEstado());
        assertEquals(120.0, reservaConfirmada.getTotal(), 0.0);
        assertTrue(unidad.getEstado() instanceof Reservada);

        double reembolso = reservaConfirmada.cancelar();
        assertEquals(EstadoReserva.CANCELADA, reservaConfirmada.getEstado());
        assertEquals(96.0, reembolso, 0.0);
    }

    @Test
    @DisplayName("S002 - Validar cadena de escalamiento completa")
    void s002_reportarIncidente_gestionarEscalamiento_cadenaCompleta() {
        Propiedad propiedad = new Propiedad("Casa Azul", "Bogotá", "No mascotas");
        Unidad unidad = new Unidad("U-100", "Casa", 120.0);
        propiedad.añadirUnidad(unidad);
        db.guardarPropiedad(propiedad);

        Reserva reserva = new Reserva(inicio, fin, huesped, unidad);
        reserva.setTotal(120.0);
        reserva.confirmar();
        db.guardarReserva(reserva);
        huesped.registrarReserva(reserva);

        Anfitrion anfitrionSoporte = new Anfitrion("A-001", "Carlos", "carlos@mail.com", "123");
        Moderador moderador = new Moderador("Ana");
        SoporteLegal soporteLegal = new SoporteLegal("Abg. Pedro");

        AnfitrionHandler handlerAnfitrion = new AnfitrionHandler(anfitrionSoporte);
        ModeradorHandler handlerModerador = new ModeradorHandler(moderador);
        SoporteLegalHandler handlerSoporte = new SoporteLegalHandler(soporteLegal);
        handlerAnfitrion.setSiguiente(handlerModerador);
        handlerModerador.setSiguiente(handlerSoporte);

        Incidente incidente = huesped.reportarIncidente("No hay agua caliente", reserva);
        assertEquals(EstadoIncidente.ABIERTO, incidente.getEstado());

        db.guardarIncidente(incidente);

        handlerAnfitrion.manejar(incidente);
        assertEquals(EstadoIncidente.CRITICO, incidente.getEstado());

        handlerSoporte.manejar(incidente, true);
        assertEquals(EstadoIncidente.RESUELTO, incidente.getEstado());

        assertTrue(db.getIncidentes().contains(incidente));
    }

    /* ---------- S003-S005: Flujos de búsqueda y reserva ---------- */

    @Test
    @DisplayName("S003 - No encontrar propiedades con servicio específico")
    void s003_buscarPorServicio_sinResultados() {
        Propiedad propiedad = new Propiedad("Casa Azul", "Bogotá", "No mascotas");
        propiedad.añadirServicio("WiFi");
        propiedad.añadirServicio("Estacionamiento");
        Unidad unidad = new Unidad("U-100", "Casa", 120.0);
        propiedad.añadirUnidad(unidad);
        db.guardarPropiedad(propiedad);

        CriterioBusqueda criterio = new CriterioBusqueda("Bogotá", 0, 0, null, Arrays.asList("Gimnasio"));
        List<Propiedad> resultados = huesped.buscarPropiedad(criterio);

        assertTrue(resultados.isEmpty());
    }

    @Test
    @DisplayName("S004 - Registro y reserva de nueva propiedad")
    void s004_registrarPropiedadBuscarReservar_integrado() {
        Propiedad propiedad = new Propiedad("Casa Verde", "Bogotá", "No fumar");
        propiedad.setPoliticaCancelacion(new PoliticaCancelacion("Flexible", 2, 0.2));
        Unidad unidad = new Unidad("U-200", "Casa", 100.0);
        propiedad.añadirUnidad(unidad);
        anfitrion.registrarPropiedad(propiedad);

        CriterioBusqueda criterio = new CriterioBusqueda("Bogotá", 0, 0, "Casa", List.of());
        List<Propiedad> resultados = huesped.buscarPropiedad(criterio);

        assertEquals(1, resultados.size());
        assertEquals("Casa Verde", resultados.get(0).getNombre());

        MetodoPago card = new TarjetaMetodoPago("4111111111111111");
        ParametrosReserva parametros = new ParametrosReserva(false, false, card);
        Reserva reserva = servicioReserva.reservar(inicio, fin, huesped, unidad, parametros);

        assertNotNull(reserva);
        assertEquals(1, db.getReservas().size());

        Reserva reservaConfirmada = db.getReservas().get(0);
        assertEquals(EstadoReserva.CONFIRMADA, reservaConfirmada.getEstado());
        assertEquals(100.0, reservaConfirmada.getTotal(), 0.0);
        assertSame(unidad, reservaConfirmada.getUnidad());
    }

    @Test
    @DisplayName("S005 - Bloqueo de solapamiento de reservas del huésped")
    void s005_dobleReservaSolapada_rechazo() {
        Propiedad prop1 = new Propiedad("Casa Azul", "Bogotá", "No mascotas");
        Unidad unidad1 = new Unidad("U-100", "Casa", 120.0);
        prop1.añadirUnidad(unidad1);
        prop1.setPoliticaCancelacion(new PoliticaCancelacion("Flexible", 2, 0.2));
        db.guardarPropiedad(prop1);

        Propiedad prop2 = new Propiedad("Casa Roja", "Medellín", "No fumar");
        Unidad unidad2 = new Unidad("U-200", "Casa", 100.0);
        prop2.añadirUnidad(unidad2);
        prop2.setPoliticaCancelacion(new PoliticaCancelacion("Flexible", 2, 0.2));
        db.guardarPropiedad(prop2);

        MetodoPago card = new TarjetaMetodoPago("4111111111111111");

        Reserva reserva1 = servicioReserva.reservar(inicio, fin, huesped, unidad1,
                new ParametrosReserva(false, false, card));
        assertNotNull(reserva1);
        assertEquals(1, db.getReservas().size());

        Reserva reserva2 = servicioReserva.reservar(inicio, fin, huesped, unidad2,
                new ParametrosReserva(false, false, card));
        assertNull(reserva2);

        assertEquals(1, db.getReservas().size());
        assertTrue(unidad2.getEstado() instanceof Disponible);
    }

    /* ---------- S006-S007: Flujos de reseñas y pagos ---------- */

    @Test
    @DisplayName("S006 - Creación y persistencia de reseña")
    void s006_crearResena_verEnSistema() {
        Propiedad propiedad = new Propiedad("Casa Azul", "Bogotá", "No mascotas");
        Unidad unidad = new Unidad("U-100", "Casa", 120.0);
        propiedad.añadirUnidad(unidad);
        db.guardarPropiedad(propiedad);

        Reserva reserva = new Reserva(inicio, fin, huesped, unidad);
        reserva.setTotal(120.0);
        reserva.confirmar();
        db.guardarReserva(reserva);
        huesped.registrarReserva(reserva);

        String comentario = "Excelente estadía, muy recomendable";
        int puntuacion = 5;
        Resena resena = servicioResena.crearResena(huesped, comentario, puntuacion, reserva);

        assertNotNull(resena);
        assertEquals(comentario, resena.getComentario());
        assertEquals(puntuacion, resena.getPuntuacion());
        assertSame(huesped, resena.getHuesped());
        assertSame(reserva, resena.getReserva());

        List<Resena> resenas = servicioResena.getResenas();
        assertEquals(1, resenas.size());
        assertTrue(resenas.contains(resena));
    }

    @Test
    @DisplayName("S007 - Rollback de unidad tras fallo de pago")
    void s007_pagoFallido_unidadSigueDisponible() {
        Propiedad propiedad = new Propiedad("Casa Azul", "Bogotá", "No mascotas");
        propiedad.setPoliticaCancelacion(new PoliticaCancelacion("Flexible", 2, 0.2));
        Unidad unidad = new Unidad("U-100", "Casa", 120.0);
        propiedad.añadirUnidad(unidad);
        db.guardarPropiedad(propiedad);

        PasarelaPago pasarelaFallida = pago -> false;
        ServicioReserva servicioFallido = new ServicioReserva(pasarelaFallida,
                new SistemaNotificacion(new Email()));

        MetodoPago cardFallida = new TarjetaMetodoPago("error");
        ParametrosReserva parametros = new ParametrosReserva(false, false, cardFallida);
        Reserva reserva = servicioFallido.reservar(inicio, fin, huesped, unidad, parametros);

        assertNull(reserva);
        assertTrue(unidad.getEstado() instanceof Disponible);
        assertTrue(db.getReservas().isEmpty());
        assertTrue(huesped.historialReservas().isEmpty());
    }
}
